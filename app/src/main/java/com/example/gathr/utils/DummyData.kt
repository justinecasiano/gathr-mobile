package com.example.gathr.utils

import com.example.gathr.data.model.DepartmentType
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.EventApprovalStatus
import com.example.gathr.data.model.EventComputedStatus
import com.example.gathr.data.model.Notification
import com.example.gathr.data.model.Participant
import com.example.gathr.data.model.ParticipantStatus
import com.example.gathr.data.model.ParticipantType
import com.example.gathr.data.model.ResponseStatus
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID


val dummyEvents: List<Event> = listOf(
    // 1. Upcoming Tech Event
    Event(
        id = 1001L,
        title = "CCIS Tech Summit 2026",
        description = "Annual technology summit featuring AI and Cloud Computing.",
        allowedDepartments = listOf(DepartmentType.CCIS),
        allowNonUmak = false,
        allowAlumni = true,
        backgroundImage = "https://picsum.photos/id/1/800/400",
        location = "University Theater",
        startTime = Instant.now().plus(5, ChronoUnit.DAYS),
        endTime = Instant.now().plus(5, ChronoUnit.DAYS).plus(4, ChronoUnit.HOURS),
        capacity = 500,
        remainingSlots = 120,
        feedbackForm = null,
        createdBy = UUID.randomUUID(),
        status = EventApprovalStatus.APPROVED,
        organizerName = "Dr. Alan Turing",
        userRole = ParticipantType.ORGANIZER, // User is the creator
        isRegistered = false,
        computedStatus = EventComputedStatus.UPCOMING,
        submittedAt = Instant.now().minus(10, ChronoUnit.DAYS),
        approvedBy = UUID.randomUUID(),
        approvedAt = Instant.now().minus(8, ChronoUnit.DAYS),
        isArchive = false
    ),

    // 2. Ongoing Workshop
    Event(
        id = 1002L,
        title = "Kotlin & Compose Workshop",
        description = "A hands-on coding session to learn Jetpack Compose.",
        allowedDepartments = listOf(DepartmentType.CCIS, DepartmentType.CITE),
        allowNonUmak = false,
        allowAlumni = false,
        backgroundImage = "https://picsum.photos/id/2/800/400",
        location = "Lab 502",
        startTime = Instant.now().minus(1, ChronoUnit.HOURS),
        endTime = Instant.now().plus(2, ChronoUnit.HOURS),
        capacity = 50,
        remainingSlots = 5,
        feedbackForm = null,
        createdBy = UUID.randomUUID(),
        status = EventApprovalStatus.APPROVED,
        organizerName = "Prof. Grace Hopper",
        userRole = ParticipantType.STAFF, // User is helping
        isRegistered = true,
        computedStatus = EventComputedStatus.ONGOING,
        submittedAt = Instant.now().minus(20, ChronoUnit.DAYS),
        approvedBy = UUID.randomUUID(),
        approvedAt = Instant.now().minus(15, ChronoUnit.DAYS),
        isArchive = false
    ),

    // 3. Pending Event
    Event(
        id = 1003L,
        title = "College Battle of the Bands",
        description = "Musical competition between colleges.",
        allowedDepartments = null,
        allowNonUmak = true,
        allowAlumni = true,
        backgroundImage = "https://picsum.photos/id/3/800/400",
        location = "Oval Grounds",
        startTime = Instant.now().plus(14, ChronoUnit.DAYS),
        endTime = Instant.now().plus(14, ChronoUnit.DAYS).plus(6, ChronoUnit.HOURS),
        capacity = 2000,
        remainingSlots = 2000,
        feedbackForm = null,
        createdBy = UUID.randomUUID(),
        status = EventApprovalStatus.PENDING,
        organizerName = "John Mayer",
        userRole = ParticipantType.ATTENDEE, // User is just looking
        isRegistered = false,
        computedStatus = EventComputedStatus.UPCOMING,
        submittedAt = Instant.now().minus(1, ChronoUnit.HOURS),
        approvedBy = null,
        approvedAt = null,
        isArchive = false
    )
)

val dummyParticipants: List<Participant> = listOf(
    // Present Attendee with Feedback
    Participant(
        eventId = 1001L,
        userId = UUID.randomUUID(),
        fullName = "Angela Cabrera",
        email = "angela@umak.edu.ph",
        displayName = "Ange",
        participantType = ParticipantType.ATTENDEE,
        participantStatus = ParticipantStatus.PRESENT,
        checkIn = Instant.now().minus(2, ChronoUnit.HOURS),
        feedbackSubmission = null,
        feedbackSubmittedAt = Instant.now().minus(1, ChronoUnit.HOURS),
        responseStatus = ResponseStatus.ANSWERED,
        rating = 5,
        comment = "Very informative.",
        joinedAt = Instant.now().minus(5, ChronoUnit.DAYS)
    ),

    // Registered Attendee (Not yet present)
    Participant(
        eventId = 1001L,
        userId = UUID.randomUUID(),
        fullName = "Ben Villanueva",
        email = "ben@umak.edu.ph",
        participantType = ParticipantType.ATTENDEE,
        participantStatus = ParticipantStatus.REGISTERED,
        responseStatus = ResponseStatus.NO_RESPONSE,
        joinedAt = Instant.now().minus(2, ChronoUnit.DAYS)
    ),

    // Staff Member
    Participant(
        eventId = 1002L,
        userId = UUID.randomUUID(),
        fullName = "Catherine Uy",
        email = "cath@umak.edu.ph",
        participantType = ParticipantType.STAFF,
        participantRole = "Lead Facilitator",
        participantStatus = ParticipantStatus.PRESENT,
        checkIn = Instant.now().minus(1, ChronoUnit.HOURS),
        responseStatus = ResponseStatus.NO_RESPONSE,
        joinedAt = Instant.now().minus(10, ChronoUnit.DAYS)
    )
)

val dummyNotifications: List<Notification> = listOf(
    // Notification 1: Registration Success (Read)
    Notification(
        id = 1,
        type = "REGISTRATION",
        message = "You have successfully registered for event: Graduation 2025.",
        isRead = true,
        createdAt = Instant.parse("2026-04-28T10:00:00Z")
    ),

    // Notification 2: Evaluation Ready (Unread)
    Notification(
        id = 2,
        type = "ACTION_REQUIRED",
        message = "Evaluation form is ready for event: CCIS Tech Summit 2026.",
        isRead = false,
        createdAt = Instant.parse("2026-05-03T15:30:00Z")
    ),

    // Notification 3: Upcoming Events Alert (Unread)
    Notification(
        id = 3,
        type = "REMINDER",
        message = "You have incoming events this week including the Kotlin Workshop.",
        isRead = false,
        createdAt = Instant.now()
    ),

    // Notification 4: Schedule Update (Read)
    Notification(
        id = 4,
        type = "ALERT",
        message = "A new schedule update has been posted for the morning sessions.",
        isRead = true,
        createdAt = Instant.parse("2026-05-02T08:15:00Z")
    ),

    // Notification 5: Event Approval (Unread)
    Notification(
        id = 5,
        type = "APPROVAL",
        message = "Your event 'Mobile Dev Meetup' has been approved by the Moderator.",
        isRead = false,
        createdAt = Instant.now().minus(2, ChronoUnit.HOURS)
    ),

    // Notification 6-11: Various Alerts (Mixed States)
    Notification(
        id = 6,
        type = "ALERT",
        message = "Room change: Kotlin Workshop moved from Lab 502 to the University Theater.",
        isRead = false,
        createdAt = Instant.parse("2026-05-04T09:00:00Z")
    ),
    Notification(
        id = 7,
        type = "ALERT",
        message = "System maintenance scheduled for this Saturday at 10:00 PM.",
        isRead = false,
        createdAt = Instant.parse("2026-05-04T13:45:00Z")
    ),
    Notification(
        id = 8,
        type = "REGISTRATION",
        message = "Registration for 'Battle of the Bands' is closing in 2 hours!",
        isRead = true,
        createdAt = Instant.parse("2026-05-01T14:20:00Z")
    ),
    Notification(
        id = 9,
        type = "ACTION_REQUIRED",
        message = "Please complete your profile to access all event features.",
        isRead = false,
        createdAt = Instant.now().minus(1, ChronoUnit.DAYS)
    ),
    Notification(
        id = 10,
        type = "ALERT",
        message = "New announcement from the Dean regarding the CCIS Tech Summit.",
        isRead = false,
        createdAt = Instant.now().minus(3, ChronoUnit.HOURS)
    ),
    Notification(
        id = 11,
        type = "REMINDER",
        message = "Don't forget to check in for the Ongoing Workshop in Lab 502.",
        isRead = false,
        createdAt = Instant.now().minus(5, ChronoUnit.MINUTES)
    )
)
