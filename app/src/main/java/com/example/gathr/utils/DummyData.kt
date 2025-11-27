package com.example.gathr.utils

import com.example.gathr.data.model.DepartmentType
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.EventApprovalStatus
import com.example.gathr.data.model.EventComputedStatus
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
        status = EventApprovalStatus.APPROVED,
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
        isArchive = true
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