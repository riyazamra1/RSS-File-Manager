package com.riyaz.rssfilemanager

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { RSSFileManagerApp() }
    }
}

private data class FileEntry(
    val file: DocumentFile,
    val name: String,
    val isDirectory: Boolean,
    val size: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RSSFileManagerApp() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    var grid by remember { mutableStateOf(false) }
    var rootUri by remember { mutableStateOf<Uri?>(null) }
    var currentUri by remember { mutableStateOf<Uri?>(null) }
    var currentTitle by remember { mutableStateOf("Local storage") }
    var sortAscending by remember { mutableStateOf(true) }

    val folderPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        } catch (_: SecurityException) {
            // Provider may grant only a subset of requested flags.
        }
        rootUri = uri
        currentUri = uri
        currentTitle = "Selected storage"
        query = ""
    }

    val currentDocument = remember(currentUri) {
        currentUri?.let { DocumentFile.fromTreeUri(context, it) }
    }

    BackHandler(enabled = currentUri != null) {
        if (currentUri == rootUri) {
            currentUri = null
            currentTitle = "Local storage"
        } else {
            currentUri = currentDocument?.parentFile?.uri ?: rootUri
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(320.dp)) {
                Spacer(Modifier.height(18.dp))
                DrawerHeader()
                Spacer(Modifier.height(12.dp))
                DrawerItem("Home", Icons.Default.Home) {
                    currentUri = null
                    currentTitle = "Local storage"
                    scope.launch { drawerState.close() }
                }
                DrawerItem("Recent files", Icons.Default.History) {
                    scope.launch { drawerState.close() }
                }
                DrawerItem("Favorites", Icons.Default.Star) {
                    scope.launch { drawerState.close() }
                }
                DrawerItem("Storage analyzer", Icons.Default.PieChart) {
                    scope.launch { drawerState.close() }
                }
                DrawerItem("Settings", Icons.Default.Settings) {
                    scope.launch { drawerState.close() }
                }
                Spacer(Modifier.weight(1f))
                Text(
                    "Razeen Secure Solution",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(20.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                currentTitle,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (currentUri != null) {
                                Text("Local storage", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (currentUri != null && currentUri != rootUri) {
                                currentUri = currentDocument?.parentFile?.uri ?: rootUri
                            } else {
                                scope.launch { drawerState.open() }
                            }
                        }) {
                            Icon(
                                if (currentUri != null && currentUri != rootUri)
                                    Icons.Default.ArrowBack else Icons.Default.Menu,
                                contentDescription = "Navigation"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { grid = !grid }) {
                            Icon(
                                if (grid) Icons.Default.ViewList else Icons.Default.GridView,
                                contentDescription = "Change view"
                            )
                        }
                        IconButton(onClick = { sortAscending = !sortAscending }) {
                            Icon(Icons.Default.SortByAlpha, contentDescription = "Sort by name")
                        }
                    }
                )
            }
        ) { padding ->
            if (currentDocument != null) {
                FileBrowser(
                    modifier = Modifier.padding(padding),
                    document = currentDocument,
                    query = query,
                    onQueryChange = { query = it },
                    grid = grid,
                    sortAscending = sortAscending,
                    onOpen = { entry ->
                        if (entry.isDirectory) {
                            currentUri = entry.file.uri
                            currentTitle = entry.name
                            query = ""
                        } else {
                            openFile(context, entry.file)
                        }
                    }
                )
            } else {
                HomeContent(
                    modifier = Modifier.padding(padding),
                    query = query,
                    onQueryChange = { query = it },
                    onPickFolder = { folderPicker.launch(null) }
                )
            }
        }
    }
}

@Composable
private fun DrawerItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(title) },
        selected = false,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = null) }
    )
}

@Composable
private fun DrawerHeader() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.onSurface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "RSS",
                    color = MaterialTheme.colorScheme.surface,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text("RSS File Manager", style = MaterialTheme.typography.titleMedium)
                Text("Razeen Secure Solution", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun HomeContent(
    modifier: Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onPickFolder: () -> Unit
) {
    Column(modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, null) },
            placeholder = { Text("Search files and folders") },
            shape = RoundedCornerShape(18.dp)
        )
        Spacer(Modifier.height(18.dp))
        Text("Storage", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(10.dp))
        StorageCard(
            title = "Internal storage",
            subtitle = "Select a folder to browse local files",
            icon = Icons.Default.Storage,
            onClick = onPickFolder
        )
        Spacer(Modifier.height(10.dp))
        StorageCard(
            title = "SD card / USB",
            subtitle = "Select an attached removable storage root",
            icon = Icons.Default.Folder,
            onClick = onPickFolder
        )
    }
}

@Composable
private fun StorageCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null)
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(3.dp))
                Text(subtitle, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun FileBrowser(
    modifier: Modifier,
    document: DocumentFile,
    query: String,
    onQueryChange: (String) -> Unit,
    grid: Boolean,
    sortAscending: Boolean,
    onOpen: (FileEntry) -> Unit
) {
    val entries = remember(document, query, sortAscending) {
        document.listFiles()
            .map {
                FileEntry(
                    file = it,
                    name = it.name ?: "Unnamed",
                    isDirectory = it.isDirectory,
                    size = if (it.isFile) it.length() else 0L
                )
            }
            .filter { query.isBlank() || it.name.contains(query.trim(), ignoreCase = true) }
            .sortedWith(
                compareBy<FileEntry> { !it.isDirectory }
                    .thenBy { if (sortAscending) it.name.lowercase() else it.name.lowercase().reversed() }
            )
    }

    Column(modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, null) },
            placeholder = { Text("Search this folder") },
            shape = RoundedCornerShape(18.dp)
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "$" + "{entries.size} item" + "$" + "{if (entries.size == 1) "" else "s"}",
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(Modifier.height(6.dp))

        if (entries.isEmpty()) {
            EmptyFolder()
        } else if (grid) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 120.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(entries) { entry -> FileGridItem(entry, onOpen) }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(entries) { entry -> FileListItem(entry, onOpen) }
            }
        }
    }
}

@Composable
private fun FileListItem(entry: FileEntry, onOpen: (FileEntry) -> Unit) {
    ListItem(
        modifier = Modifier.clip(RoundedCornerShape(16.dp)).clickable { onOpen(entry) },
        leadingContent = {
            Icon(
                if (entry.isDirectory) Icons.Default.Folder else Icons.Default.InsertDriveFile,
                contentDescription = null
            )
        },
        headlineContent = {
            Text(entry.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        supportingContent = {
            Text(if (entry.isDirectory) "Folder" else formatSize(entry.size))
        }
    )
}

@Composable
private fun FileGridItem(entry: FileEntry, onOpen: (FileEntry) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(130.dp).clickable { onOpen(entry) },
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                if (entry.isDirectory) Icons.Default.Folder else Icons.Default.InsertDriveFile,
                contentDescription = null,
                modifier = Modifier.size(42.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                entry.name,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun EmptyFolder() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(56.dp))
            Spacer(Modifier.height(10.dp))
            Text("No files found", style = MaterialTheme.typography.titleMedium)
        }
    }
}

private fun openFile(context: android.content.Context, file: DocumentFile) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(file.uri, file.type ?: "*/*")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    runCatching { context.startActivity(intent) }
}

private fun formatSize(bytes: Long): String {
    if (bytes < 1024) return "$" + "{bytes} B"
    val kb = bytes / 1024.0
    if (kb < 1024) return String.format("%.1f KB", kb)
    val mb = kb / 1024.0
    if (mb < 1024) return String.format("%.1f MB", mb)
    return String.format("%.1f GB", mb / 1024.0)
}
