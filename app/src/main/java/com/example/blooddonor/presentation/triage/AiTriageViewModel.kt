package com.example.blooddonor.presentation.triage

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TriageMessage(
    val text: String,
    val isUser: Boolean
)

class AiTriageViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<TriageMessage>>(
        listOf(
            TriageMessage(
                text = "Hello! I am your AI Clinical Triage Specialist. Ask me about any medical condition, medication, lifestyle factor, or eligibility question.",
                isUser = false
            )
        )
    )
    val messages: StateFlow<List<TriageMessage>> = _messages.asStateFlow()

    fun sendMessage(query: String) {
        val userMsg = TriageMessage(text = query, isUser = true)
        val aiResponseText = generateSpecialistResponse(query.lowercase())
        val aiMsg = TriageMessage(text = aiResponseText, isUser = false)

        _messages.value = _messages.value + userMsg + aiMsg
    }

    private fun generateSpecialistResponse(query: String): String {
        return when {
            // Alcohol & Substances
            query.contains("drunk") || query.contains("alcohol") || query.contains("beer") || query.contains("wine") || query.contains("drinking") ->
                "🩺 Clinical Triage Assessment (Substance Use):\n\n" +
                        "• Status: Temporary Deferral.\n" +
                        "• Guidelines: Donors must be completely sober, free of alcohol influence, and well-hydrated. Alcohol consumption requires a strict 24-hour deferral prior to blood donation to prevent cardiovascular complications during extraction."

            // Blood Cancers & Major Malignancies
            query.contains("cancer") || query.contains("tumor") || query.contains("leukemia") || query.contains("lymphoma") ->
                "🩺 Clinical Triage Assessment (Oncology):\n\n" +
                        "• Status: Conditional / Permanent Deferral.\n" +
                        "• Guidelines: A history of blood cancers (leukemia, lymphoma, myeloma) results in a permanent deferral. For localized solid tumors (e.g., skin cancer treated successfully), eligibility is typically restored 1 to 5 years post-treatment depending on pathology and clearance."

            // Tattoos, Piercings, Acupuncture
            query.contains("tattoo") || query.contains("piercing") || query.contains("acupuncture") || query.contains("ink") ->
                "🩺 Clinical Triage Assessment (Dermatological Procedures):\n\n" +
                        "• Status: Temporary Deferral.\n" +
                        "• Guidelines: Getting a tattoo, body piercing, or acupuncture requires a 6-month waiting period from the date of the procedure. This mitigates exposure risks to bloodborne pathogens like Hepatitis B and C."

            // Weight & Physical Metrics
            query.contains("weight") || query.contains("kg") || query.contains("pounds") || query.contains("heavy") || query.contains("light") ->
                "🩺 Clinical Triage Assessment (Biometric Criteria):\n\n" +
                        "• Status: Threshold Evaluation.\n" +
                        "• Guidelines: Standard whole-blood donation requires a minimum body weight of 50 kg (approx. 110 lbs). Donors below this threshold are at higher risk of acute hypovolemic reactions (dizziness, fainting)."

            // Blood Pressure & Hypertension
            query.contains("blood pressure") || query.contains("bp") || query.contains("hypertension") || query.contains("hypotension") ->
                "🩺 Clinical Triage Assessment (Cardiovascular/BP):\n\n" +
                        "• Status: Vital Sign Dependent.\n" +
                        "• Guidelines: Systolic blood pressure must be between 90 and 180 mmHg, and diastolic pressure between 50 and 100 mmHg at the time of donation. Controlled hypertension on stable medication is acceptable; unstable readings require deferral."

            // Diabetes & Insulin
            query.contains("diabetes") || query.contains("sugar") || query.contains("insulin") || query.contains("diabetic") ->
                "🩺 Clinical Triage Assessment (Endocrine Health):\n\n" +
                        "• Status: Conditional Eligibility.\n" +
                        "• Guidelines: Individuals with well-controlled diabetes managed by diet, oral hypoglycemics, or non-bovine insulin are generally eligible. Use of bovine (beef-source) insulin is permanently deferred due to variant Creutzfeldt-Jakob disease risks."

            // Infections, Fever, Antibiotics
            query.contains("fever") || query.contains("cold") || query.contains("flu") || query.contains("antibiotic") || query.contains("infection") || query.contains("sick") ->
                "🩺 Clinical Triage Assessment (Acute Illness):\n\n" +
                        "• Status: Temporary Deferral.\n" +
                        "• Guidelines: Donors must be entirely symptom-free, healthy, and fever-free on donation day. Active bacterial infections and courses of therapeutic antibiotics require full completion of treatment plus a symptom-free buffer period."

            // Medications (General / Blood Thinners)
            query.contains("medicine") || query.contains("medication") || query.contains("pills") || query.contains("aspirin") || query.contains("blood thinner") ->
                "🩺 Clinical Triage Assessment (Pharmacology):\n\n" +
                        "• Status: Medication-Specific Evaluation.\n" +
                        "• Guidelines: While most common medications (e.g., birth control, basic vitamins) are acceptable, specific drugs like blood thinners (Warfarin, Heparin), acne treatments (Accutane/Isotretinoin), and anti-platelet drugs require strict waiting periods (ranging from 48 hours to months)."

            // Default Specialist Response
            else ->
                "🩺 Clinical Triage Assessment (General Screening):\n\n" +
                        "• Status: General Evaluation Required.\n" +
                        "• Guidelines: Based on standard hematology and blood bank safety protocols, ensure you feel well-rested, hydrated, and have consumed a nutritious meal. Please share specific details (such as medications, recent surgeries, or symptoms) for tailored clinical advice."
        }
    }
}