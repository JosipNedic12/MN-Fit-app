package com.example.mnfit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mnfit.model.Term
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TermsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _terms = MutableStateFlow<List<Term>>(emptyList())
    val terms: StateFlow<List<Term>> = _terms.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedTerm = MutableStateFlow<Term?>(null)
    val selectedTerm: StateFlow<Term?> = _selectedTerm.asStateFlow()

    private val _participantNames = MutableStateFlow<List<String>>(emptyList())
    val participantNames: StateFlow<List<String>> = _participantNames.asStateFlow()

    private var listenerRegistration: com.google.firebase.firestore.ListenerRegistration? = null

    init {
        listenForTerms()
    }


    private fun listenForTerms() {
        _isLoading.value = true
        listenerRegistration = db.collection("terms")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    _terms.value = snapshot.toObjects(Term::class.java)
                    _isLoading.value = false
                }
            }
    }

    fun selectTerm(term: Term) {
        _selectedTerm.value = term
        fetchParticipantNames(term.participants)
    }

    fun addTerm(term: Term, onResult: (Boolean, String) -> Unit) {
        db.collection("terms")
            .document(term.termId)
            .set(term)
            .addOnSuccessListener { onResult(true, "Term added!") }
            .addOnFailureListener { onResult(false, "Failed to add term.") }
    }

    fun clearSelectedTerm() {
        _selectedTerm.value = null
        _participantNames.value = emptyList()
    }

    fun signUpForTerm(term: Term, onResult: (Boolean, String) -> Unit) {
        val currentUserUid = _currentUserUid.value
        if (term.participants.contains(currentUserUid)) {
            onResult(false, "Already signed up.")
            return
        }
        if (term.participants.size >= term.maxParticipants) {
            onResult(false, "Term is full.")
            return
        }
        val newParticipants = term.participants + currentUserUid
        db.collection("terms").document(term.termId)
            .update("participants", newParticipants)
            .addOnSuccessListener {
                db.collection("terms").document(term.termId).get()
                    .addOnSuccessListener { doc ->
                        val updatedTerm = doc.toObject(Term::class.java)
                        if (updatedTerm != null) {
                            _selectedTerm.value = updatedTerm
                            fetchParticipantNames(updatedTerm.participants)
                        }
                        onResult(true, "Signed up!")
                    }
            }
            .addOnFailureListener { onResult(false, "Failed to sign up.") }
    }

    fun signOutFromTerm(term: Term, onResult: (Boolean, String) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        if (!term.participants.contains(currentUser.uid)) {
            onResult(false, "You are not signed up.")
            return
        }
        val newParticipants = term.participants.filter { it != currentUser.uid }
        FirebaseFirestore.getInstance()
            .collection("terms")
            .document(term.termId)
            .update("participants", newParticipants)
            .addOnSuccessListener { onResult(true, "You have left the term.") }
            .addOnFailureListener { onResult(false, "Failed to leave the term.") }
    }
    private val _currentUserUid = MutableStateFlow<String?>(FirebaseAuth.getInstance().currentUser?.uid)
    val currentUserUid: StateFlow<String?> = _currentUserUid.asStateFlow()

    fun refreshCurrentUser() {
        _currentUserUid.value = FirebaseAuth.getInstance().currentUser?.uid
    }
    fun isUserSignedUp(term: Term): Boolean {
        return term.participants.contains(_currentUserUid.value)
    }

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole.asStateFlow()

    fun fetchUserRole(currentUserId: String?) {
        if (currentUserId == null) {
            _userRole.value = null
            return
        }
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(currentUserId)
            .get()
            .addOnSuccessListener { doc ->
                _userRole.value = doc.getString("role")
            }
            .addOnFailureListener {
                _userRole.value = null // or handle/log error
            }
    }

    private var userRoleListener: ListenerRegistration? = null

    fun listenForUserRole(currentUserId: String?) {
        userRoleListener?.remove()
        if (currentUserId == null) {
            _userRole.value = null
            return
        }
        userRoleListener = FirebaseFirestore.getInstance()
            .collection("users")
            .document(currentUserId)
            .addSnapshotListener { snapshot, _ ->
                _userRole.value = snapshot?.getString("role")
            }
    }

    private fun fetchParticipantNames(uids: List<String>) {
        viewModelScope.launch {
            val names = mutableListOf<String>()
            for (uid in uids) {
                val userDoc = db.collection("users").document(uid).get().await()
                val name = userDoc.getString("firstName") ?: "Unknown"
                names.add(name)
            }
            _participantNames.value = names
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
