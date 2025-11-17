package com.example.gathr.presentation.moderator
import com.example.gathr.R
object EventDataSource {
    val pendingEvents = listOf(
        EventData(
            id = 1,
            title = "University of Makati’s Infotechnolympics",
            location = "UMak Auditorium, Makati City",
            dateTime = "Oct 5, 2025 | 9:00 AM to 5:00 PM",
            organizer = "Prof. Era Ganaban",
            capacity = 1200,
            slots = 21,
            countRegistered = 420,
            countCancelled = 69,
            imageRes = R.drawable.sample_event,
            status = "New",
            type = "Significant Event",
            host = "CCIS",
            attachmentName = "Infotechnolympics_Excuse_Letter.pdf",
            shortDate = "Oct. 5, 2025",
            timeRange = "9:00 AM to 5:00 PM",
            description = "UMak Infotechnolympics is an annual event showcasing students' IT skills."
        )
        // Add more events if you want
    )
}
