/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.android.navigation.databinding.FragmentGameBinding

class GameFragment : Fragment() {
    data class Question(
            val text: String,
            val answers: List<String>)

    /*  The first answer is the correct one.  We randomize the answers before showing the text.
    All questions must have four answers.  We'd want these to contain references to string
    resources so we could internationalize. (Or better yet, don't define the questions in code...)*/
    private val questions: MutableList<Question> = mutableListOf(
            Question(text = "Which file extension is used to save Kotlin files?",
                    answers = listOf(".kt or .kts", ".kot", ".java", ".c")),
            Question(text = "How to make a multi lined comment in Kotlin?",
                    answers = listOf("/* */", "/ /", "//", "## ##")),
            Question(text = "How do you get the length of a string in Kotlin?",
                    answers = listOf("str.length", "length(str)", "str.lengthOf", "strlen(str)")),
            Question(text = "Which of these is used to handle null exceptions in Kotlin?",
                    answers = listOf("Elvis Operator", "Range", "Lambda function", "Sealed Class")),
            Question(text = "What is the default behavior of Kotlin classes?",
                    answers = listOf("All classes are final", "All classes are public", "All classes are sealed", "All classes are abstract")),
            Question(text = "Which of these is true for Kotlin variables?",
                    answers = listOf("val corresponds to final variable in Java", "var cannot be changed", "val can be changed", "All variables are immutable by default")),
            Question(text = "Kotlin is developed by?",
                    answers = listOf("JetBrains", "Google", "Apple", "Microsoft")),
            Question(text = "Which other programing language is Kotlin most compatible with?",
                    answers = listOf("Java", "c", "Python", "Ruby")),
            Question(text = "Which of these features is only found in Java, not Kotlin?",
                    answers = listOf("Static members", "Null Safety", "Operator Overloading", "Smart Casts")),
            Question(text = "What are Kotlin coroutines?",
                    answers = listOf("They provide asynchronous code without thread blocking.", "It's Kotlin's term for class methods",
                            "They are functions which accept other functions as arguments", "Kotlin's term for macros"))
    )


    lateinit var currentQuestion: Question
    lateinit var answers: MutableList<String>
    private var questionIndex = 0
    private val numQuestions = Math.min((questions.size + 1) / 2, 5)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentGameBinding>(
                inflater, R.layout.fragment_game, container, false)

        // Shuffles the questions and sets the question index to the first question.
        randomizeQuestions()

        // Bind this fragment class to the layout
        binding.game = this

        // Set the onClickListener for the submitButton
        binding.submitButton.setOnClickListener @Suppress("UNUSED_ANONYMOUS_PARAMETER")
        { view: View ->
            val checkedId = binding.questionRadioGroup.checkedRadioButtonId
            // Do nothing if nothing is checked (id == -1)
            if (-1 != checkedId) {
                var answerIndex = 0
                when (checkedId) {
                    R.id.secondAnswerRadioButton -> answerIndex = 1
                    R.id.thirdAnswerRadioButton -> answerIndex = 2
                    R.id.fourthAnswerRadioButton -> answerIndex = 3
                }
                // The first answer in the original question is always the correct one, so if our
                // answer matches, we have the correct answer.
                if (answers[answerIndex] == currentQuestion.answers[0]) {
                    questionIndex++
                    // Advance to the next question
                    if (questionIndex < numQuestions) {
                        currentQuestion = questions[questionIndex]
                        setQuestion()
                        binding.invalidateAll()
                    } else {
                        // We've won!  Navigate to the gameWonFragment.
                        view.findNavController()
                                .navigate(GameFragmentDirections
                                        .actionGameFragmentToGameWonFragment(numQuestions, questionIndex))
                    }
                } else {
                    // Game over! A wrong answer sends us to the gameOverFragment.
                    view.findNavController()
                            .navigate(GameFragmentDirections.actionGameFragmentToGameOverFragment())
                }
            }
        }
        return binding.root
    }

    // randomize the questions and set the first question
    private fun randomizeQuestions() {
        questions.shuffle()
        questionIndex = 0
        setQuestion()
    }

    // Sets the question and randomizes the answers.  This only changes the data, not the UI.
    // Calling invalidateAll on the FragmentGameBinding updates the data.
    private fun setQuestion() {
        currentQuestion = questions[questionIndex]
        // randomize the answers into a copy of the array
        answers = currentQuestion.answers.toMutableList()
        // and shuffle them
        answers.shuffle()
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_android_trivia_question, questionIndex + 1, numQuestions)
    }
}
