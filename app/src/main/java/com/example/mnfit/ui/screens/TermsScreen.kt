package com.example.mnfit.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mnfit.R
import com.example.mnfit.ui.theme.gym_Blue
import com.example.mnfit.viewmodel.TermsViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.res.stringResource

@Composable
fun TermsScreen(
    navController: NavController,
    termsViewModel: TermsViewModel = viewModel()
) {
    val terms by termsViewModel.terms.collectAsState()
    val isLoading by termsViewModel.isLoading.collectAsState()
    val selectedTerm by termsViewModel.selectedTerm.collectAsState()
    val participantNames by termsViewModel.participantNames.collectAsState()
    val currentUserUid by termsViewModel.currentUserUid.collectAsState()
    val userRole by termsViewModel.userRole.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var signUpMsg by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(currentUserUid) {
        termsViewModel.refreshCurrentUser()
        termsViewModel.listenForUserRole(currentUserUid)
        termsViewModel.removeExpiredTerms()
        termsViewModel.listenForTerms()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    stringResource(R.string.terms),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(50.dp)
                        .weight(1f),
                    alignment = Alignment.TopEnd
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(terms.sortedBy { it.date }) { term ->
                        TermCard(
                            term = term,
                            isUserSignedUp = termsViewModel.isUserSignedUp(term),
                            onClick = { termsViewModel.selectTerm(term) },
                            onSignUpClick = {
                                termsViewModel.signUpForTerm(term) { success, msg -> /* handle result */ }
                            },
                            onSignOutClick = {
                                termsViewModel.signOutFromTerm(term) { success, msg -> /* handle result */ }
                            }
                        )
                    }
                }
            }
        }
        if (userRole == "trainer" || userRole == "owner") {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                containerColor = gym_Blue
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_term))
            }
        }
        if (showAddDialog) {
            AddTermDialog(
                onDismiss = { showAddDialog = false },
                onAddTerm = { term ->
                    termsViewModel.addTerm(term) { success, msg ->
                        showAddDialog = false
                    }
                },
                trainerId = currentUserUid ?: ""
            )
        }

        selectedTerm?.let { term ->
            if (terms.any { it.termId == term.termId }) {
                TermDetailsDialog(
                    term = term,
                    currentUserUid = currentUserUid,
                    participantNames = participantNames,
                    isTrainer = userRole != "subscriber",
                    onDismiss = {
                        signUpMsg = null
                        termsViewModel.clearSelectedTerm()
                    },
                    onRemoveTerm = { term ->
                        termsViewModel.removeTerm(term.termId)
                        signUpMsg = null
                        termsViewModel.clearSelectedTerm()
                    },
                    onSignUp = { t ->
                        termsViewModel.signUpForTerm(t) { success, msg ->
                            signUpMsg = msg
                            if (success) {
                                val updatedTerm = terms.find { it.termId == t.termId }
                                if (updatedTerm != null) {
                                    termsViewModel.selectTerm(updatedTerm)
                                }
                            }
                        }
                    },
                    signUpMsg = signUpMsg
                )
            }
        }
    }
}






