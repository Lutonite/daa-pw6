package ch.heigvd.iict.daa.rest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ch.heigvd.iict.daa.rest.ui.screens.AppContact
import ch.heigvd.iict.daa.rest.ui.theme.MyComposeApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyComposeApplicationTheme {
                AppContact(application = application as ContactsApplication)
            }
        }
    }
}