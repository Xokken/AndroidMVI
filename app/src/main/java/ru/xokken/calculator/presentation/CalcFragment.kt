package ru.xokken.calculator.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import ru.xokken.calculator.databinding.CalcFragmentBinding
import ru.xokken.calculator.presentation.model.*
import ru.xokken.calculator.util.getInt
import ru.xokken.calculator.util.setValue

class CalcFragment : Fragment() {

    private lateinit var viewModel: CalcViewModel
    private var _binding: CalcFragmentBinding? = null
    private val binding get() = _binding!!
    private val actionSubject: Subject<Action> = PublishSubject.create()
    private val disposables = CompositeDisposable()

    private lateinit var firstTextWatcher: TextWatcher
    private lateinit var secondTextWatcher: TextWatcher
    private lateinit var thirdTextWatcher: TextWatcher

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[CalcViewModel::class.java]
        _binding = CalcFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeState(actionSubject.hide())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { state ->
                with(binding) {
                    withoutTextWatcher(etFirst, firstTextWatcher) {
                        etFirst.setValue(state.firstInput)
                    }
                    withoutTextWatcher(etSecond, firstTextWatcher) {
                        etSecond.setValue(state.secondInput)
                    }
                    withoutTextWatcher(etThird, thirdTextWatcher) {
                        etThird.setValue(state.thirdInput)
                    }
                    pb.isVisible = state.isLoading
                }
            }.run {
                disposables.add(this)
            }
    }

    override fun onStart() {
        super.onStart()
        with(binding) {
            firstTextWatcher = createTextWatcher {
                it?.getInt()?.let { value ->
                    actionSubject.onNext(FirstInput(value))
                }
            }
            etFirst.addTextChangedListener(firstTextWatcher)
            secondTextWatcher = createTextWatcher {
                it?.getInt()?.let { value ->
                    actionSubject.onNext(SecondInput(value))
                }
            }
            etSecond.addTextChangedListener(secondTextWatcher)
            thirdTextWatcher = createTextWatcher {
                it?.getInt()?.let { value ->
                    if (value < 0) actionSubject.onNext(
                        DiffResInput(
                            value,
                            getAllInputsInfo()
                        )
                    )
                    else actionSubject.onNext(ThirdInput(value))
                }
            }
            etThird.addTextChangedListener(thirdTextWatcher)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getAllInputsInfo(): InputInfo =
        with(binding) {
            InputInfo(
                etFirst.text?.getInt(),
                etSecond.text?.getInt(),
                etThird.text?.getInt()
            )
        }

    private fun createTextWatcher(listener: (Editable?) -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                listener.invoke(p0)
            }
        }
    }

    private inline fun withoutTextWatcher(
        editText: EditText,
        textWatcher: TextWatcher,
        codeBlock: () -> Unit
    ) {
        editText.removeTextChangedListener(textWatcher)
        codeBlock()
        editText.addTextChangedListener(textWatcher)
    }

    companion object {
        fun newInstance() = CalcFragment()
    }
}
