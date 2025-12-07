package com.example.gathr.utils

import com.example.gathr.data.model.DepartmentType
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.EventApprovalStatus
import com.example.gathr.data.model.EventComputedStatus
import com.example.gathr.data.model.Notification
import com.example.gathr.data.model.Participant
import com.example.gathr.data.model.ParticipantStatus
import com.example.gathr.data.model.ParticipantType
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

// --- Enum Definitions (Assumed based on your code) ---

// --- The Dummy Data List ---
val dummyEvents = listOf(
    // 1. An Upcoming Tech Event (Approved)
    Event(
        id = 1001L,
        parentEventId = null,
        title = "CCIS Tech Summit 2025",
        description = "Annual technology summit featuring the latest in AI, Mobile Development, and Cloud Computing. Open to all CCIS students.",
        roles = listOf("Student", "Faculty"),
        allowedDepartments = listOf(),
        allowNonUmak = false,
        allowAlumni = true,
        backgroundImage = "https://picsum.photos/id/1/800/400", // Placeholder Image
        location = "University Theater",
        startTime = Instant.now().plus(5, ChronoUnit.DAYS), // 5 days from now
        endTime = Instant.now().plus(5, ChronoUnit.DAYS).plus(4, ChronoUnit.HOURS),
        capacity = 500,
        remainingSlots = 120,
        evaluationForm = "https://forms.gle/xyz",
        createdBy = UUID.randomUUID(),
        createdByName = "Dr. Alan Turing",
        status = EventApprovalStatus.PENDING,
        computedStatus = EventComputedStatus.UPCOMING,
        submittedAt = Instant.now().minus(10, ChronoUnit.DAYS),
        approvedBy = UUID.randomUUID(),
        approvedAt = Instant.now().minus(8, ChronoUnit.DAYS),
        isArchive = false
    ),

    // 2. An Ongoing Workshop (Happening Now)
    Event(
        id = 1002L,
        parentEventId = 1001L, // Sub-event of the Tech Summit
        title = "Kotlin for Beginners Workshop",
        description = "A hands-on coding session to learn the basics of Kotlin and Jetpack Compose.",
        roles = listOf("Student"),
        allowedDepartments = listOf(),
        allowNonUmak = false,
        allowAlumni = false,
        backgroundImage = "https://picsum.photos/id/2/800/400",
        location = "Lab 502, Building 3",
        startTime = Instant.now().minus(1, ChronoUnit.HOURS), // Started 1 hour ago
        endTime = Instant.now().plus(2, ChronoUnit.HOURS), // Ends in 2 hours
        capacity = 50,
        remainingSlots = 5,
        evaluationForm = null,
        createdBy = UUID.randomUUID(),
        createdByName = "Prof. Grace Hopper",
        status = EventApprovalStatus.APPROVED,
        computedStatus = EventComputedStatus.ONGOING,
        submittedAt = Instant.now().minus(20, ChronoUnit.DAYS),
        approvedBy = UUID.randomUUID(),
        approvedAt = Instant.now().minus(15, ChronoUnit.DAYS),
        isArchive = false
    ),

    // 3. A Pending Request (Draft)
    Event(
        id = 1003L,
        parentEventId = null,
        title = "College Battle of the Bands",
        description = "A musical competition between different colleges. We need approval for the venue.",
        roles = listOf("Student", "Organizer"),
        allowedDepartments = listOf(),
        allowNonUmak = true,
        allowAlumni = true,
        backgroundImage = "https://picsum.photos/id/3/800/400",
        location = "Oval Grounds",
        startTime = Instant.now().plus(14, ChronoUnit.DAYS),
        endTime = Instant.now().plus(14, ChronoUnit.DAYS).plus(6, ChronoUnit.HOURS),
        capacity = 2000,
        remainingSlots = 2000,
        evaluationForm = null,
        createdBy = UUID.randomUUID(),
        createdByName = "John Mayer",
        status = EventApprovalStatus.PENDING,
        computedStatus = EventComputedStatus.UPCOMING, // Even if pending, it's in future
        submittedAt = Instant.now().minus(1, ChronoUnit.HOURS),
        approvedBy = null,
        approvedAt = null,
        isArchive = false
    ),

    // 4. A Completed Event (Archived)
    Event(
        id = 1004L,
        parentEventId = null,
        title = "Alumni Homecoming 2023",
        description = "Looking back at the memories. A night of gathering for Batch 2023.",
        roles = listOf("Alumni"),
        allowedDepartments = null, // Open to all departments
        allowNonUmak = false,
        allowAlumni = true,
        backgroundImage = "https://picsum.photos/id/4/800/400",
        location = "Grand Ballroom",
        startTime = Instant.now().minus(365, ChronoUnit.DAYS), // 1 year ago
        endTime = Instant.now().minus(365, ChronoUnit.DAYS).plus(5, ChronoUnit.HOURS),
        capacity = 300,
        remainingSlots = 0,
        evaluationForm = "https://forms.gle/old",
        createdBy = UUID.randomUUID(),
        createdByName = "Admin Staff",
        status = EventApprovalStatus.APPROVED,
        computedStatus = EventComputedStatus.COMPLETED,
        submittedAt = Instant.now().minus(400, ChronoUnit.DAYS),
        approvedBy = UUID.randomUUID(),
        approvedAt = Instant.now().minus(390, ChronoUnit.DAYS),
        isArchive = false
    ),

    // 5. Rejected Event
    Event(
        id = 1005L,
        parentEventId = null,
        title = "Unauthorized midnight party",
        description = "Just a random meetup.",
        roles = listOf("Student"),
        allowedDepartments = listOf(),
        allowNonUmak = true,
        allowAlumni = false,
        backgroundImage = "https://picsum.photos/id/5/800/400",
        location = "Rooftop",
        startTime = Instant.now().plus(2, ChronoUnit.DAYS),
        endTime = Instant.now().plus(2, ChronoUnit.DAYS).plus(2, ChronoUnit.HOURS),
        capacity = 20,
        remainingSlots = 20,
        evaluationForm = null,
        createdBy = UUID.randomUUID(),
        createdByName = "Rebellious Student",
        status = EventApprovalStatus.REJECTED,
        computedStatus = EventComputedStatus.UPCOMING,
        submittedAt = Instant.now().minus(2, ChronoUnit.HOURS),
        approvedBy = UUID.randomUUID(), // Rejected by this person
        approvedAt = Instant.now().minus(1, ChronoUnit.HOURS),
        comment = "This violates university curfew policies.",
        isArchive = false
    )
)
val dummyParticipants: List<Participant> = listOf(
    // -----------------------------------------------------------
    // Scenario 1: PRESENT Attendee (Completed Evaluation)
    // -----------------------------------------------------------
    Participant(
        eventId = 101,
        userId = UUID.fromString("1a1a1a1a-1111-4000-8000-000000000001"),
        fullName = "Angela Cabrera",
        participantType = ParticipantType.ATTENDEE,
        participantRole = null,
        participantStatus = ParticipantStatus.PRESENT,
        checkIn = Instant.parse("2025-11-20T09:30:00Z"),
        evaluation = "Excellent event organization!",
        evaluationSubmittedAt = Instant.parse("2025-11-20T17:00:00Z"),
        joinedAt = Instant.parse("2025-10-15T10:00:00Z")
    ),

    // -----------------------------------------------------------
    // Scenario 2: ABSENT Attendee (Did not check in)
    // -----------------------------------------------------------
    Participant(
        eventId = 101,
        userId = UUID.fromString("2b2b2b2b-2222-4000-8000-000000000002"),
        fullName = "Ben Villanueva",
        participantType = ParticipantType.ATTENDEE,
        participantRole = null,
        participantStatus = ParticipantStatus.PRESENT,
        checkIn = null,
        evaluation = null,
        evaluationSubmittedAt = null,
        joinedAt = Instant.parse("2025-10-01T14:00:00Z")
    ),

    // -----------------------------------------------------------
    // Scenario 3: REGISTERED Attendee (Awaiting event start)
    // -----------------------------------------------------------
    Participant(
        eventId = 102,
        userId = UUID.fromString("3c3c3c3c-3333-4000-8000-000000000003"),
        fullName = "Catherine Uy",
        participantType = ParticipantType.ATTENDEE,
        participantRole = null,
        participantStatus = ParticipantStatus.REGISTERED,
        checkIn = null,
        evaluation = null,
        evaluationSubmittedAt = null,
        joinedAt = Instant.parse("2025-11-25T12:00:00Z")
    ),

    // -----------------------------------------------------------
    // Scenario 4: CANCELLED Attendee
    // -----------------------------------------------------------
    Participant(
        eventId = 103,
        userId = UUID.fromString("4d4d4d4d-4444-4000-8000-000000000004"),
        fullName = "Daniel Ramirez",
        participantType = ParticipantType.ATTENDEE,
        participantRole = null,
        participantStatus = ParticipantStatus.CANCELLED,
        checkIn = null,
        evaluation = null,
        evaluationSubmittedAt = null,
        joinedAt = Instant.parse("2025-11-05T08:00:00Z")
    ),

    // -----------------------------------------------------------
    // Scenario 5: PRESENT Attendee (Evaluation Pending)
    // -----------------------------------------------------------
    Participant(
        eventId = 101,
        userId = UUID.fromString("5e5e5e5e-5555-4000-8000-000000000005"),
        fullName = "Ethan Lopez",
        participantType = ParticipantType.ATTENDEE,
        participantRole = null,
        participantStatus = ParticipantStatus.PRESENT,
        checkIn = Instant.parse("2025-11-20T10:15:00Z"),
        evaluation = null,
        evaluationSubmittedAt = null,
        joinedAt = Instant.parse("2025-10-25T13:00:00Z")
    ),


    Participant(
        eventId = 101,
        userId = UUID.randomUUID(),
        fullName = "Ethan Lopez",
        participantType = ParticipantType.ATTENDEE,
        participantRole = null,
        participantStatus = ParticipantStatus.PRESENT,
        checkIn = Instant.parse("2025-11-20T10:15:00Z"),
        evaluation = null,
        evaluationSubmittedAt = null,
        joinedAt = Instant.parse("2025-10-25T13:00:00Z")
    ),
    Participant(
        eventId = 101,
        userId = UUID.randomUUID(),
        fullName = "Ethan Lopez",
        participantType = ParticipantType.ATTENDEE,
        participantRole = null,
        participantStatus = ParticipantStatus.PRESENT,
        checkIn = Instant.parse("2025-11-20T10:15:00Z"),
        evaluation = null,
        evaluationSubmittedAt = null,
        joinedAt = Instant.parse("2025-10-25T13:00:00Z")
    ),
    Participant(
        eventId = 101,
        userId = UUID.randomUUID(),
        fullName = "Ethan Lopez",
        participantType = ParticipantType.ATTENDEE,
        participantRole = null,
        participantStatus = ParticipantStatus.PRESENT,
        checkIn = Instant.parse("2025-11-20T10:15:00Z"),
        evaluation = null,
        evaluationSubmittedAt = null,
        joinedAt = Instant.parse("2025-10-25T13:00:00Z")
    ),
    Participant(
        eventId = 101,
        userId = UUID.randomUUID(),
        fullName = "Ethan Lopez",
        participantType = ParticipantType.ATTENDEE,
        participantRole = null,
        participantStatus = ParticipantStatus.PRESENT,
        checkIn = Instant.parse("2025-11-20T10:15:00Z"),
        evaluation = null,
        evaluationSubmittedAt = null,
        joinedAt = Instant.parse("2025-10-25T13:00:00Z")
    ),
    Participant(
        eventId = 101,
        userId = UUID.randomUUID(),
        fullName = "Ethan Lopez",
        participantType = ParticipantType.ATTENDEE,
        participantRole = null,
        participantStatus = ParticipantStatus.PRESENT,
        checkIn = Instant.parse("2025-11-20T10:15:00Z"),
        evaluation = null,
        evaluationSubmittedAt = null,
        joinedAt = Instant.parse("2025-10-25T13:00:00Z")
    ),
    Participant(
        eventId = 101,
        userId = UUID.randomUUID(),
        fullName = "Ethan Lopez",
        participantType = ParticipantType.ATTENDEE,
        participantRole = null,
        participantStatus = ParticipantStatus.PRESENT,
        checkIn = Instant.parse("2025-11-20T10:15:00Z"),
        evaluation = null,
        evaluationSubmittedAt = null,
        joinedAt = Instant.parse("2025-10-25T13:00:00Z")
    ),
    Participant(
        eventId = 101,
        userId = UUID.randomUUID(),
        fullName = "Ethan Lopez",
        participantType = ParticipantType.ATTENDEE,
        participantRole = null,
        participantStatus = ParticipantStatus.PRESENT,
        checkIn = Instant.parse("2025-11-20T10:15:00Z"),
        evaluation = null,
        evaluationSubmittedAt = null,
        joinedAt = Instant.parse("2025-10-25T13:00:00Z")
    ),
    Participant(
        eventId = 101,
        userId = UUID.randomUUID(),
        fullName = "Ethan Lopez",
        participantType = ParticipantType.ATTENDEE,
        participantRole = null,
        participantStatus = ParticipantStatus.PRESENT,
        checkIn = Instant.parse("2025-11-20T10:15:00Z"),
        evaluation = null,
        evaluationSubmittedAt = null,
        joinedAt = Instant.parse("2025-10-25T13:00:00Z")
    )
)

val dummyNotifications: List<Notification> = listOf(
    // -----------------------------------------------------------
    // Notification 1: Registration Success (Read)
    // -----------------------------------------------------------
    Notification(
        id = 1,
        type = "REGISTRATION",
        message = "You have successfully registered for event: Graduation 2025.",
        isRead = true,
        // Set an arbitrary past time
        createdAt = Instant.parse("2025-11-28T10:00:00Z")
    ),

    // -----------------------------------------------------------
    // Notification 2: Evaluation Ready (Unread)
    // -----------------------------------------------------------
    Notification(
        id = 2,
        type = "ACTION_REQUIRED",
        message = "Evaluation form is ready for event: Tekla vs Manny Pacquiao alumni match.",
        isRead = false,
        // Set a more recent time
        createdAt = Instant.parse("2025-12-03T15:30:00Z")
    ),

    // -----------------------------------------------------------
    // Notification 3: Upcoming Events Alert (Unread)
    // -----------------------------------------------------------
    Notification(
        id = 3,
        type = "REMINDER",
        message = "You have incoming events this week including one significant event.",
        isRead = false,
        // Set the current time
        createdAt = Instant.now()
    ),

    // -----------------------------------------------------------
    // Notification 4: Arbitrary Alert (Read)
    // -----------------------------------------------------------
    Notification(
        id = 4,
        type = "ALERT",
        message = "A new schedule update has been posted for the morning sessions.",
        isRead = true,
        createdAt = Instant.parse("2025-12-02T08:15:00Z")
    ),
    Notification(
        id = 5,
        type = "ALERT",
        message = "A new schedule update has been posted for the morning sessions.",
        isRead = false,
        createdAt = Instant.parse("2025-12-02T08:15:00Z")
    ),
    Notification(
        id = 6,
        type = "ALERT",
        message = "A new schedule update has been posted for the morning sessions.",
        isRead = false,
        createdAt = Instant.parse("2025-12-02T08:15:00Z")
    ),
    Notification(
        id = 7,
        type = "ALERT",
        message = "A new schedule update has been posted for the morning sessions.",
        isRead = false,
        createdAt = Instant.parse("2025-12-02T08:15:00Z")
    ),
    Notification(
        id = 8,
        type = "ALERT",
        message = "A new schedule update has been posted for the morning sessions.",
        isRead = true,
        createdAt = Instant.parse("2025-12-02T08:15:00Z")
    ),
    Notification(
        id = 9,
        type = "ALERT",
        message = "A new schedule update has been posted for the morning sessions.",
        isRead = false,
        createdAt = Instant.parse("2025-12-02T08:15:00Z")
    ),
    Notification(
        id = 10,
        type = "ALERT",
        message = "A new schedule update has been posted for the morning sessions.",
        isRead = false,
        createdAt = Instant.parse("2025-12-02T08:15:00Z")
    ),
    Notification(
        id = 11,
        type = "ALERT",
        message = "A new schedule update has been posted for the morning sessions.",
        isRead = false,
        createdAt = Instant.parse("2025-12-02T08:15:00Z")
    )
)