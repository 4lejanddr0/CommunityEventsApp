package com.tuempresa.communityeventsapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.tuempresa.communityeventsapp.ui.auth.LoginScreen
import com.tuempresa.communityeventsapp.ui.events.EventDetailScreen
import com.tuempresa.communityeventsapp.ui.events.EventDetailViewModel
import com.tuempresa.communityeventsapp.ui.events.EventFormScreen
import com.tuempresa.communityeventsapp.ui.events.EventsListScreen
import com.tuempresa.communityeventsapp.ui.events.EventsViewModel

object Routes {
    const val LOGIN = "login"
    const val EVENTS = "events"
    const val EVENT_FORM = "event_form"
    const val EVENT_DETAIL = "event_detail"
    const val EVENT_ID_ARG = "id"
}

@Composable
fun AppNavGraph(startDestination: String = Routes.LOGIN) {
    val nav = rememberNavController()

    // Si ya hay sesión, salta directo a eventos
    LaunchedEffect(Unit) {
        if (FirebaseAuth.getInstance().currentUser != null) {
            nav.navigate(Routes.EVENTS) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
        }
    }

    NavHost(navController = nav, startDestination = startDestination) {

        // -------- LOGIN --------
        composable(Routes.LOGIN) {
            LoginScreen(onLoggedIn = { /* usamos AuthStateListener abajo */ })

            // Listener de sesión para ir a EVENTS
            DisposableEffect(Unit) {
                val auth = FirebaseAuth.getInstance()
                val listener = FirebaseAuth.AuthStateListener { a ->
                    if (a.currentUser != null) {
                        nav.navigate(Routes.EVENTS) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                }
                auth.addAuthStateListener(listener)
                onDispose { auth.removeAuthStateListener(listener) }
            }
        }

        // -------- LISTA DE EVENTOS --------
        composable(Routes.EVENTS) {
            val vm: EventsViewModel = hiltViewModel()
            EventsListScreen(
                viewModel = vm,
                onOpen = { id ->
                    nav.navigate("${Routes.EVENT_DETAIL}/$id")
                },
                onCreate = {
                    nav.navigate("${Routes.EVENT_FORM}/new")
                },
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    nav.navigate(Routes.LOGIN) {
                        popUpTo(Routes.EVENTS) { inclusive = true }
                    }
                }
            )
        }

        // -------- DETALLE DE EVENTO --------
        composable(
            route = "${Routes.EVENT_DETAIL}/{${Routes.EVENT_ID_ARG}}",
            arguments = listOf(
                navArgument(Routes.EVENT_ID_ARG) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString(Routes.EVENT_ID_ARG) ?: return@composable
            val vm: EventDetailViewModel = hiltViewModel()

            EventDetailScreen(
                eventId = eventId,
                vm = vm,
                onBack = { nav.popBackStack() },
                onEdit = { id ->
                    nav.navigate("${Routes.EVENT_FORM}/$id")
                }
            )
        }

        // -------- FORMULARIO (NUEVO / EDITAR) --------
        composable(
            route = "${Routes.EVENT_FORM}/{${Routes.EVENT_ID_ARG}}",
            arguments = listOf(
                navArgument(Routes.EVENT_ID_ARG) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val arg = backStackEntry.arguments?.getString(Routes.EVENT_ID_ARG)
            val vm: EventsViewModel = hiltViewModel()

            EventFormScreen(
                viewModel = vm,
                eventId = arg?.takeUnless { it == "new" },
                onDone = { nav.popBackStack() }
            )
        }
    }
}
