package com.golazo.medical.data

import kotlin.math.roundToInt
import kotlin.random.Random

object SimulatedData {

    // Home Dashboard
    data class PerformanceMetric(val label: String, val value: String, val trend: String, val trendUp: Boolean)

    val performanceMetrics = listOf(
        PerformanceMetric("Sprint Speed", "32.4 km/h", "+2.1%", true),
        PerformanceMetric("Distance/Match", "10.8 km", "+0.3 km", true),
        PerformanceMetric("Pass Accuracy", "87%", "-1.2%", false),
        PerformanceMetric("Tackles Won", "73%", "+5%", true)
    )

    data class RecoveryData(val label: String, val value: Int, val max: Int)

    val recoveryData = listOf(
        RecoveryData("Sleep Quality", 82, 100),
        RecoveryData("Muscle Recovery", 75, 100),
        RecoveryData("Hydration", 90, 100),
        RecoveryData("Readiness Score", 78, 100)
    )

    data class ScheduleItem(val day: String, val activity: String, val time: String)

    val weeklySchedule = listOf(
        ScheduleItem("Mon", "Team Training", "09:00"),
        ScheduleItem("Tue", "Gym Session", "10:00"),
        ScheduleItem("Wed", "Tactical Review", "14:00"),
        ScheduleItem("Thu", "Team Training", "09:00"),
        ScheduleItem("Fri", "Recovery", "11:00"),
        ScheduleItem("Sat", "Match Day", "15:00"),
        ScheduleItem("Sun", "Rest", "—")
    )

    data class LoadStat(val label: String, val thisWeek: Int, val lastWeek: Int)

    val loadManagement = listOf(
        LoadStat("Total Distance (km)", 48, 45),
        LoadStat("High-Speed Runs", 34, 29),
        LoadStat("Training Load (AU)", 1850, 1720),
        LoadStat("Minutes Played", 180, 210)
    )

    // Wellbeing Bio Signals
    data class BioSignals(
        val heartRate: Int,
        val stressLevel: String,
        val stressValue: Int,
        val hrv: Int,
        val coherence: Int
    )

    fun generateBioSignals(hour: Int): BioSignals {
        val baseHr = when {
            hour < 8 -> 62
            hour < 12 -> 68
            hour < 17 -> 72
            hour < 21 -> 66
            else -> 60
        }
        val hr = baseHr + Random.nextInt(-2, 3)

        val (stressLabel, stressVal) = when {
            hour < 8 -> "Calm" to Random.nextInt(15, 30)
            hour < 12 -> "Normal" to Random.nextInt(30, 50)
            hour < 17 -> "Moderate" to Random.nextInt(50, 70)
            hour < 21 -> "Normal" to Random.nextInt(35, 55)
            else -> "Calm" to Random.nextInt(10, 25)
        }

        val hrv = when {
            hour < 8 -> Random.nextInt(55, 75)
            hour < 12 -> Random.nextInt(45, 60)
            hour < 17 -> Random.nextInt(35, 50)
            else -> Random.nextInt(50, 65)
        }

        val coherence = when {
            hour < 8 -> Random.nextInt(70, 90)
            hour < 12 -> Random.nextInt(55, 75)
            hour < 17 -> Random.nextInt(40, 60)
            else -> Random.nextInt(60, 80)
        }

        return BioSignals(hr, stressLabel, stressVal, hrv, coherence)
    }

    // Weekly Trends
    val weeklyHrData = listOf(64, 68, 72, 70, 66, 74, 62)
    val weeklyStressData = listOf(25, 35, 55, 45, 40, 65, 20)
    val weeklyHrvData = listOf(62, 55, 42, 48, 58, 38, 68)

    data class BeforeAfter(val metric: String, val before: String, val after: String, val improved: Boolean)

    val beforeAfterData = listOf(
        BeforeAfter("Heart Rate", "78 bpm", "64 bpm", true),
        BeforeAfter("Stress Level", "62%", "28%", true),
        BeforeAfter("Coherence", "45%", "82%", true)
    )

    // Sessions Library
    data class WellbeingSession(
        val id: String,
        val title: String,
        val duration: Int,
        val description: String,
        val category: String,
        val steps: List<String>
    )

    val sessionsLibrary = listOf(
        WellbeingSession("s1", "Pre-Match Focus", 10, "Center your mind before the game with focused breathing and visualization.", "Pre-match Focus", listOf("Find a quiet space", "Close your eyes", "Take 5 deep breaths", "Visualize your best performance", "Set your intention for the match")),
        WellbeingSession("s2", "Tactical Clarity", 8, "Clear mental fog and sharpen decision-making.", "Pre-match Focus", listOf("Sit comfortably", "Focus on your breathing", "Visualize key tactical scenarios", "Practice mental rehearsal", "Open your eyes with clarity")),
        WellbeingSession("s3", "Halftime Reset", 5, "Quick mental reset during the break.", "Halftime Reset", listOf("Take 3 deep breaths", "Release first-half tension", "Refocus on second-half goals", "Positive self-talk", "Return energized")),
        WellbeingSession("s4", "Momentum Shift", 4, "Regain control when the game isn't going your way.", "Halftime Reset", listOf("Acknowledge frustration", "Box breathing: 4-4-4-4", "Reset your mindset", "Focus on what you can control")),
        WellbeingSession("s5", "Post-Loss Processing", 12, "Process difficult emotions after a tough result.", "Post-loss Debrief", listOf("Acknowledge your feelings", "Body scan for tension", "Guided reflection", "Identify one positive", "Set recovery intention")),
        WellbeingSession("s6", "Team Resilience", 10, "Build mental resilience as a team.", "Post-loss Debrief", listOf("Group breathing exercise", "Share one challenge", "Reframe the narrative", "Collective goal setting")),
        WellbeingSession("s7", "Deep Recovery", 15, "Full body and mind recovery session.", "Recovery Day", listOf("Progressive muscle relaxation", "Body scan meditation", "Gratitude practice", "Sleep preparation visualization", "Set tomorrow's intention")),
        WellbeingSession("s8", "Active Rest", 8, "Light mental exercise for rest days.", "Recovery Day", listOf("Gentle stretching with breath", "Mindful walking visualization", "Positive affirmations", "Rest without guilt")),
        WellbeingSession("s9", "Sleep Optimizer", 12, "Prepare your mind for quality sleep.", "Recovery Day", listOf("Dim your environment", "4-7-8 breathing pattern", "Body scan relaxation", "Counting visualization", "Drift into sleep")),
        WellbeingSession("s10", "Media Confidence", 7, "Prepare for press conferences and interviews.", "Media Prep", listOf("Centering breath", "Visualize confident responses", "Practice key messages", "Grounding exercise")),
        WellbeingSession("s11", "Social Media Detox", 6, "Mental reset from online pressure.", "Media Prep", listOf("Put phone away", "Grounding: 5 senses exercise", "Positive self-reflection", "Set boundaries", "Return to present"))
    )

    // Psychologist Directory
    data class Psychologist(
        val id: String,
        val name: String,
        val title: String,
        val specialties: List<String>,
        val languages: List<String>,
        val rating: Float,
        val sessionCount: Int,
        val available: Boolean,
        val nextSlot: String,
        val color: Long,
        val availableSlots: List<String>
    )

    val psychologists = listOf(
        Psychologist("p1", "Dr. Sarah Mitchell", "Sports Psychologist", listOf("Performance Anxiety", "Team Dynamics"), listOf("English", "French"), 4.9f, 240, true, "Today, 14:00", 0xFF1A4B8C, listOf("14:00", "15:30", "17:00")),
        Psychologist("p2", "Dr. Marco Rossi", "Clinical Psychologist", listOf("Injury Recovery", "Depression"), listOf("English", "Italian", "Spanish"), 4.8f, 185, true, "Tomorrow, 10:00", 0xFF4CAF50, listOf("10:00", "11:30", "14:00", "16:00")),
        Psychologist("p3", "Dr. Anna Bergström", "Performance Coach", listOf("Mental Resilience", "Focus Training"), listOf("English", "Swedish", "German"), 4.7f, 156, false, "Wed, 09:00", 0xFFFF9800, listOf("09:00", "10:30", "13:00")),
        Psychologist("p4", "Dr. James Okafor", "Wellbeing Specialist", listOf("Stress Management", "Sleep"), listOf("English", "Yoruba"), 4.9f, 210, true, "Today, 16:30", 0xFF9C27B0, listOf("16:30", "18:00"))
    )

    data class TherapySession(val psychologist: String, val date: String, val time: String, val type: String, val isToday: Boolean)

    val upcomingSessions = listOf(
        TherapySession("Dr. Sarah Mitchell", "Today", "14:00", "Video", true),
        TherapySession("Dr. Marco Rossi", "Thursday", "10:00", "Voice Call", false)
    )

    // Breathing Protocols
    data class BreathingProtocol(
        val id: String,
        val name: String,
        val description: String,
        val inhale: Int,
        val hold1: Int,
        val exhale: Int,
        val hold2: Int,
        val rounds: Int
    )

    val breathingProtocols = listOf(
        BreathingProtocol("box", "Box Breathing", "Equal phases for calm focus", 4, 4, 4, 4, 4),
        BreathingProtocol("coherence", "Coherence Breathing", "Balanced 5-5 for HRV optimization", 5, 0, 5, 0, 6),
        BreathingProtocol("calm", "4-7-8 Calm", "Extended exhale for deep relaxation", 4, 7, 8, 0, 4)
    )

    // Intelligence Graph Nodes
    data class GraphNode(
        val id: String,
        val type: String,
        val label: String,
        val x: Float,
        val y: Float,
        val color: Long
    )

    data class GraphEdge(val from: String, val to: String)

    val graphNodes = listOf(
        GraphNode("n1", "player", "M. Salah", 0.3f, 0.2f, 0xFF1A4B8C),
        GraphNode("n2", "player", "K. Mbappé", 0.7f, 0.3f, 0xFF1A4B8C),
        GraphNode("n3", "team", "Liverpool FC", 0.2f, 0.5f, 0xFF4CAF50),
        GraphNode("n4", "team", "Real Madrid", 0.8f, 0.5f, 0xFF4CAF50),
        GraphNode("n5", "injury", "ACL Tear", 0.5f, 0.15f, 0xFFF44336),
        GraphNode("n6", "formation", "4-3-3", 0.4f, 0.7f, 0xFFFF9800),
        GraphNode("n7", "game", "UCL Final", 0.6f, 0.7f, 0xFF9C27B0),
        GraphNode("n8", "pattern", "Counter Attack", 0.15f, 0.8f, 0xFF00BCD4),
        GraphNode("n9", "situation", "Set Piece", 0.85f, 0.8f, 0xFFFFEB3B),
        GraphNode("n10", "outcome", "Goal Scored", 0.5f, 0.9f, 0xFF8BC34A)
    )

    val graphEdges = listOf(
        GraphEdge("n1", "n3"), GraphEdge("n2", "n4"),
        GraphEdge("n1", "n5"), GraphEdge("n3", "n6"),
        GraphEdge("n4", "n6"), GraphEdge("n3", "n7"),
        GraphEdge("n4", "n7"), GraphEdge("n6", "n8"),
        GraphEdge("n7", "n9"), GraphEdge("n8", "n10"),
        GraphEdge("n9", "n10"), GraphEdge("n1", "n7"),
        GraphEdge("n2", "n7")
    )

    // Wellbeing recommendations
    data class Recommendation(val title: String, val description: String, val category: String, val sessionId: String)

    fun getRecommendation(dayOfWeek: Int, hour: Int): Recommendation {
        return when {
            dayOfWeek == 6 -> Recommendation("Pre-Match Focus", "Game day! Center your mind for peak performance.", "Pre-match Focus", "s1")
            dayOfWeek == 7 || dayOfWeek == 1 -> Recommendation("Deep Recovery", "Rest day — prioritize your mental recovery.", "Recovery Day", "s7")
            hour < 12 -> Recommendation("Tactical Clarity", "Morning session to sharpen your focus.", "Pre-match Focus", "s2")
            hour < 17 -> Recommendation("Halftime Reset", "Afternoon reset to maintain energy.", "Halftime Reset", "s3")
            else -> Recommendation("Sleep Optimizer", "Wind down for quality rest tonight.", "Recovery Day", "s9")
        }
    }
}
