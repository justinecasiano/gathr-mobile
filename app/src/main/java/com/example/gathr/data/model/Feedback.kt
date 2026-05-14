package com.example.gathr.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
sealed class FeedbackQuestion {
    abstract val id: String
    abstract val type: String
    abstract val questionText: String
    abstract val required: Boolean
    abstract val order: Int
    abstract val isMandatory: Boolean?

    @Serializable
    @SerialName("radio")
    data class RadioQuestion(
        override val id: String,
        override val questionText: String,
        override val required: Boolean,
        override val order: Int,
        override val isMandatory: Boolean? = false,
        val options: List<FormOption>,
        override val type: String = "radio"
    ) : FeedbackQuestion()

    @Serializable
    @SerialName("checkbox")
    data class CheckboxQuestion(
        override val id: String,
        override val questionText: String,
        override val required: Boolean,
        override val order: Int,
        override val isMandatory: Boolean? = false,
        val options: List<FormOption>,
        override val type: String = "checkbox"
    ) : FeedbackQuestion()

    @Serializable
    @SerialName("slider")
    data class SliderQuestion(
        override val id: String,
        override val questionText: String,
        override val required: Boolean,
        override val order: Int,
        override val isMandatory: Boolean? = false,
        val maxRating: Int = 5,
        override val type: String = "slider"
    ) : FeedbackQuestion()

    @Serializable
    @SerialName("text_input")
    data class TextQuestion(
        override val id: String,
        override val questionText: String,
        override val required: Boolean,
        override val order: Int,
        override val isMandatory: Boolean? = false,
        val placeholder: String? = null,
        override val type: String = "text_input"
    ) : FeedbackQuestion()
}

@Serializable
data class FormOption(val id: String, val label: String)

@Serializable
data class FormEditorValues(
    val title: String,
    val questions: List<FeedbackQuestion>
)

@Serializable
data class QuestionResponse(
    val questionId: String,
    val answer: JsonElement
)

@Serializable
data class FormSubmission(
    val eventId: String,
    val responses: List<QuestionResponse>,
    val submittedAt: String
)