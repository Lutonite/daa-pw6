package ch.heigvd.iict.daa.rest.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.heigvd.iict.daa.rest.ContactsApplication
import ch.heigvd.iict.daa.rest.R
import ch.heigvd.iict.daa.rest.models.Contact
import ch.heigvd.iict.daa.rest.viewmodels.ContactsViewModel
import ch.heigvd.iict.daa.rest.viewmodels.ContactsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContact(application: ContactsApplication, contactsViewModel : ContactsViewModel = viewModel(factory= ContactsViewModelFactory(application))) {
    val context = LocalContext.current
    val contacts: List<Contact> by contactsViewModel.allContacts.observeAsState(initial = emptyList())
    var editionMode by remember { mutableStateOf(false) }
    var selectedContact by remember { mutableStateOf<Contact?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = {
                        contactsViewModel.enroll()
                    }) { Icon(painter = painterResource(R.drawable.populate), contentDescription = null) }
                    IconButton(onClick = {
                        contactsViewModel.refresh()
                    }) { Icon(painter = painterResource(R.drawable.synchronize), contentDescription = null) }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                selectedContact = null
                editionMode = true
            }){
                Icon(Icons.Default.Add, contentDescription = null)
            }
        },
    )
    { padding ->
        Column(modifier = Modifier.padding(top = padding.calculateTopPadding())) {
            if (editionMode) {
                ScreenContactsEditor(
                    contactsViewModel = contactsViewModel,
                    contact = selectedContact,
                    onNavigateBack = {
                        editionMode = false
                    }
                )
            } else {
                ScreenContactList(contacts) { contact ->
                    selectedContact = contact
                    editionMode = true
                }
            }
        }
    }

}