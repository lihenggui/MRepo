package com.sanmer.mrepo.ui.screens.settings.repositories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sanmer.mrepo.R
import com.sanmer.mrepo.database.entity.Repo

@Composable
fun RepositoriesList(
    list: List<Repo>,
    state: LazyListState,
    update: (Repo) -> Unit,
    delete: (Repo) -> Unit,
    getUpdate: (Repo, (Throwable) -> Unit) -> Unit
) = LazyColumn(
    state = state,
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(10.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
) {
    items(
        items = list,
        key = { it.url }
    ) { repo ->
        val value by remember(list) { mutableStateOf(repo) }

        RepositoryItem(
            repo = value,
            toggle = { update(it) },
            onUpdate = getUpdate,
            onDelete = delete,
        )
    }
}

@Composable
private fun RepositoryItem(
    repo: Repo,
    toggle: (Repo) -> Unit,
    onUpdate: (Repo, (Throwable) -> Unit) -> Unit,
    onDelete: (Repo) -> Unit,
) {
    var delete by remember { mutableStateOf(false) }
    if (delete) DeleteDialog(
        repo = repo,
        onClose = { delete = false },
        onConfirm = { onDelete(repo) }
    )

    var failure by remember { mutableStateOf(false) }
    var message: String? by remember { mutableStateOf(null) }
    if (failure) FailureDialog(
        repo = repo,
        message = message,
        onClose = {
            failure = false
            message = null
        }
    )

    RepositoryItem(
        repo = repo,
        toggle = {
            toggle(repo.copy(enable = it))
        },
        update = {
            onUpdate(repo) {
                failure = true
                message = it.message
            }
        },
        delete = { delete = true }
    )
}

@Composable
private fun DeleteDialog(
    repo: Repo,
    onClose: () -> Unit,
    onConfirm: () -> Unit
) = AlertDialog(
    shape = RoundedCornerShape(20.dp),
    onDismissRequest = onClose,
    title = { Text(text = stringResource(id = R.string.dialog_attention)) },
    text = {
        Text(text = stringResource(id = R.string.repo_delete_dialog_desc, repo.name))
    },
    confirmButton = {
        TextButton(
            onClick = {
                onConfirm()
                onClose()
            }
        ) {
            Text(text = stringResource(id = R.string.repo_options_delete))
        }
    },
    dismissButton = {
        TextButton(
            onClick = onClose
        ) {
            Text(text = stringResource(id = R.string.dialog_cancel))
        }
    }
)

@Composable
fun FailureDialog(
    repo: Repo,
    message: String?,
    onClose: () -> Unit
) = AlertDialog(
    shape = RoundedCornerShape(20.dp),
    onDismissRequest = onClose,
    title = { Text(text = repo.name) },
    text = {
        Text(
            text = message.toString(),
            maxLines = 5,
            overflow = TextOverflow.Ellipsis
        )
    },
    confirmButton = {
        TextButton(
            onClick = onClose
        ) {
            Text(text = stringResource(id = R.string.dialog_ok))
        }
    }
)