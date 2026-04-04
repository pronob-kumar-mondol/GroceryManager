package com.grocery.manager.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.grocery.manager.data.local.Company
import com.grocery.manager.data.local.Contact
import com.grocery.manager.viewmodel.CompanyViewModel
import androidx.compose.foundation.lazy.itemsIndexed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyScreen(
    companyViewModel: CompanyViewModel,
    onNavigateBack: () -> Unit
) {
    val companies by companyViewModel.companies.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var companyToEdit by remember { mutableStateOf<Company?>(null) }

    if (showAddDialog || companyToEdit != null) {
        AddEditCompanyDialog(
            company = companyToEdit,
            companyViewModel = companyViewModel,
            onDismiss = {
                showAddDialog = false
                companyToEdit = null
            },
            onSave = { company, contacts ->
                if (companyToEdit != null) {
                    companyViewModel.updateCompanyWithContacts(company, contacts)
                } else {
                    companyViewModel.insertCompanyWithContacts(company, contacts)
                }
                showAddDialog = false
                companyToEdit = null
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Companies",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Company",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        if (companies.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.outlineVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No companies yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tap + to add your first company",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(companies, key = { it.id }) { company ->
                    CompanyCard(
                        company = company,
                        companyViewModel = companyViewModel,
                        onEdit = { companyToEdit = company },
                        onDelete = { companyViewModel.deleteCompany(company) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CompanyCard(
    company: Company,
    companyViewModel: CompanyViewModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val contacts by companyViewModel
        .getContactsForCompany(company.id)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    var showDeleteDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Company") },
            text = { Text("Delete \"${company.name}\"? Products linked to this company will not be deleted.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Business,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = company.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Contacts section
            if (contacts.isNotEmpty()) {
                TextButton(
                    onClick = { expanded = !expanded },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = if (expanded) "Hide contacts"
                        else "${contacts.size} contact${if (contacts.size > 1) "s" else ""}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess
                        else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                if (expanded) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    contacts.forEach { contact ->
                        ContactRow(
                            contact = contact,
                            onCallClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${contact.phone}")
                                }
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactRow(
    contact: Contact,
    onCallClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (contact.role.isNotBlank()) {
                Text(
                    text = contact.role,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = contact.phone,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(onClick = onCallClick) {
            Icon(
                Icons.Default.Call,
                contentDescription = "Call",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// Form state for a single contact row in the dialog
data class ContactFormState(
    val id: Int = 0,
    val name: String = "",
    val role: String = "",
    val phone: String = ""
)

@Composable
private fun AddEditCompanyDialog(
    company: Company?,
    companyViewModel: CompanyViewModel,
    onDismiss: () -> Unit,
    onSave: (Company, List<Contact>) -> Unit
) {
    var name by remember { mutableStateOf(company?.name ?: "") }
    var nameError by remember { mutableStateOf(false) }
    var contacts by remember { mutableStateOf(listOf(ContactFormState())) }

    // Load existing contacts in edit mode
    val existingContacts by if (company != null)
        companyViewModel.getContactsForCompany(company.id)
            .collectAsStateWithLifecycle(initialValue = emptyList())
    else
        remember { mutableStateOf(emptyList()) }

    LaunchedEffect(existingContacts) {
        if (existingContacts.isNotEmpty()) {
            contacts = existingContacts.map { c ->
                ContactFormState(
                    id = c.id,
                    name = c.name,
                    role = c.role,
                    phone = c.phone
                )
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (company != null) "Edit Company" else "Add Company",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; nameError = false },
                        label = { Text("Company Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = nameError,
                        supportingText = { if (nameError) Text("Name is required") },
                        singleLine = true
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Contacts",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(
                            onClick = {
                                contacts = contacts + ContactFormState()
                            }
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add")
                        }
                    }
                }

                itemsIndexed(contacts) { index, contact ->
                    ContactFormRow(
                        contact = contact,
                        showDelete = contacts.size > 1,
                        onContactChange = { updated ->
                            contacts = contacts.toMutableList().also { it[index] = updated }
                        },
                        onDelete = {
                            contacts = contacts.toMutableList().also { it.removeAt(index) }
                        }
                    )
                    if (index < contacts.lastIndex) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                        return@TextButton
                    }
                    val companyObj = Company(
                        id = company?.id ?: 0,
                        name = name.trim()
                    )
                    val contactList = contacts
                        .filter { it.name.isNotBlank() && it.phone.isNotBlank() }
                        .map { c ->
                            Contact(
                                id = c.id,
                                companyId = company?.id ?: 0,
                                name = c.name.trim(),
                                role = c.role.trim(),
                                phone = c.phone.trim()
                            )
                        }
                    onSave(companyObj, contactList)
                }
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun ContactFormRow(
    contact: ContactFormState,
    showDelete: Boolean,
    onContactChange: (ContactFormState) -> Unit,
    onDelete: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = contact.name,
                onValueChange = { onContactChange(contact.copy(name = it)) },
                label = { Text("Name") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            if (showDelete) {
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = contact.role,
                onValueChange = { onContactChange(contact.copy(role = it)) },
                label = { Text("Role") },
                placeholder = { Text("Dealer, SR...") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = contact.phone,
                onValueChange = { onContactChange(contact.copy(phone = it)) },
                label = { Text("Phone") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )
        }
    }
}