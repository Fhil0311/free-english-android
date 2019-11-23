package io.github.freeenglish.ViewModels

import androidx.lifecycle.*
import io.github.freeenglish.questions.AskUserUseCase
import io.github.freeenglish.questions.Question
import kotlinx.coroutines.launch

class QuestionsViewModel(private val askUserUseCase: AskUserUseCase) : ViewModel() {
    private val _state: MutableLiveData<ScreenState> = MutableLiveData()
    val state: LiveData<ScreenState> get() = _state

    private var _currentState: ScreenState.QuestionState? = null

    init {
        viewModelScope.launch {
            val state = ScreenState.QuestionState(askUserUseCase.askQuestion())
            _state.value = state
            _currentState = state
        }
    }

    fun onAnswerClick(answerPos: Int) {
        viewModelScope.launch {
            if (_currentState != null && _currentState!!.question.answers.size > answerPos) {
                val correctAnswer = _currentState!!.question.correctAnswer.id == _currentState!!.question.answers[answerPos].id
                _state.value = if (correctAnswer) {
                    ScreenState.CorrectAnswer
                }
                else {
                    ScreenState.WrongAnswer(
                        word = _currentState!!.question.question,
                        meaning = _currentState!!.question.correctAnswer.meaning,
                        examples = _currentState!!.question.correctAnswer.examples
                    )
                }
            }
        }


    }

    fun onNextClick() {
        viewModelScope.launch {
            val state = ScreenState.QuestionState(askUserUseCase.askQuestion())
            _state.value = state
            _currentState = state

        }
    }
}


sealed class ScreenState() {
    data class QuestionState(val question: Question): ScreenState()
    object CorrectAnswer: ScreenState()
    data class WrongAnswer(val word: String, val meaning: String, val examples: String): ScreenState()
}

