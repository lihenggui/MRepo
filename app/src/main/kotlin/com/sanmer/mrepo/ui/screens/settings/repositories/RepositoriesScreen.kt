package com.sanmer.mrepo.ui.screens.settings.repositories

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sanmer.mrepo.R
import com.sanmer.mrepo.database.entity.toRepo
import com.sanmer.mrepo.ui.animate.slideInTopToBottom
import com.sanmer.mrepo.ui.animate.slideOutBottomToTop
import com.sanmer.mrepo.ui.component.NavigateUpTopBar
import com.sanmer.mrepo.ui.component.PageIndicator
import com.sanmer.mrepo.ui.component.TextFieldDialog
import com.sanmer.mrepo.ui.utils.isScrollingUp
import com.sanmer.mrepo.ui.utils.none
import com.sanmer.mrepo.viewmodel.RepositoriesViewModel

@Composable
fun RepositoriesScreen(
    navController: NavController,
    viewModel: RepositoriesViewModel = hiltViewModel()
) {
    val list by viewModel.list.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listSate = rememberLazyListState()
    val showFab = listSate.isScrollingUp()

    var value by remember { mutableStateOf("https://your-repo.com/".toRepo()) }
    var message: String? by remember { mutableStateOf(null) }

    var failure by remember { mutableStateOf(false) }
    if (failure) FailureDialog(
        repo = value,
        message = message,
        onClose = { failure = false }
    )

    var add by remember { mutableStateOf(false) }
    if (add) AddDialog(
        onClose = { add = false },
        onAdd = {
            value = it.toRepo()
            viewModel.insert(value) { e ->
                failure = true
                message = e.message
            }
        }
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                scrollBehavior = scrollBehavior,
                navController = navController,
                onRefresh = viewModel::getRepoAll
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = showFab,
                enter = scaleIn(
                    animationSpec = tween(100),
                    initialScale = 0.8f
                ),
                exit = scaleOut(
                    animationSpec = tween(100),
                    targetScale = 0.8f
                )
            ) {
                FloatingButton { add = true }
            }
        },
        contentWindowInsets = WindowInsets.none
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            if (list.isEmpty()) {
                PageIndicator(
                    icon = R.drawable.git_pull_request,
                    text = R.string.repo_empty
                )
            }

            RepositoriesList(
                list = list,
                state = listSate,
                update = viewModel::update,
                delete = viewModel::delete,
                getUpdate = viewModel::getUpdate
            )

            AnimatedVisibility(
                visible = viewModel.progress,
                enter = slideInTopToBottom(),
                exit = slideOutBottomToTop()
            ) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
private fun AddDialog(
    onClose: () -> Unit,
    onAdd: (String) -> Unit
) {
    var url by remember { mutableStateOf("") }

    val onDone: () -> Unit = {
        if (!url.endsWith("/")) url += "/"

        onAdd(url)
        onClose()
    }

    TextFieldDialog(
        shape = RoundedCornerShape(20.dp),
        onDismissRequest = onClose,
        title = { Text(text = stringResource(id = R.string.repo_add_dialog_title)) },
        confirmButton = {
            TextButton(
                onClick = onDone,
                enabled = url.isNotBlank()
            ) {
                Text(text = stringResource(id = R.string.repo_add_dialog_add))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onClose
            ) {
                Text(text = stringResource(id = R.string.dialog_cancel))
            }
        }
    ) { focusRequester ->
        OutlinedTextField(
            modifier = Modifier.focusRequester(focusRequester),
            textStyle = MaterialTheme.typography.bodyLarge,
            value = url,
            onValueChange = { url = it },
            placeholder = { Text(text = "https://your-repo.com/") },
            singleLine = false,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions {
                if (url.isNotBlank()) onDone()
            },
            shape = RoundedCornerShape(15.dp)
        )
    }
}

@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    navController: NavController,
    onRefresh: () -> Unit
) = NavigateUpTopBar(
    title = stringResource(id = R.string.settings_repo),
    actions = {
        IconButton(
            onClick = onRefresh
        ) {
            Icon(
                painter = painterResource(id = R.drawable.refresh),
                contentDescription = null
            )
        }
    },
    scrollBehavior = scrollBehavior,
    navController = navController
)

@Composable
private fun FloatingButton(
    onClick: () -> Unit
) = FloatingActionButton(
    onClick = onClick,
    contentColor = MaterialTheme.colorScheme.onPrimary,
    containerColor = MaterialTheme.colorScheme.primary
) {
    Icon(
        painter = painterResource(id = R.drawable.pencil_plus),
        contentDescription = null
    )
}