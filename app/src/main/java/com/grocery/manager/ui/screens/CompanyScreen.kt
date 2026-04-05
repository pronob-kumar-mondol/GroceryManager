package com.grocery.manager.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.grocery.manager.data.local.Company
import com.grocery.manager.data.local.Contact
import com.grocery.manager.ui.theme.Teal500
import com.grocery.manager.ui.theme.TextSecondary
import com.grocery.manager.viewmodel.CompanyViewModel

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
        containerColor = MaterialTheme.colorScheme.background,
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
                            contentDescription = "Back",
                            tint = Teal500
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Teal500,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Company")
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
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                color = Teal500.copy(alpha = 0.1f),
                                shape = MaterialTheme.shapes.extraLarge
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = null,
                            tint = Teal500.copy(alpha = 0.6f),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "No companies yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap + to add your first company",
                        fontSize = 13.sp,
                        color = TextSecondary,
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
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text("Delete Company", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Delete \"${company.name}\"? Products linked to this company will not be deleted.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Delete", fontWeight = FontWeight.Bold) }
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
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Teal left accent border
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(Teal500)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Business,
                            contentDescription = null,
                            tint = Teal500,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = company.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Row {
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = Teal500,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Contacts section
                if (contacts.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = { expanded = !expanded },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            tint = Teal500,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${contacts.size} contact${if (contacts.size > 1) "s" else ""}",
                            fontSize = 12.sp,
                            color = Teal500,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess
                            else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = Teal500,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    if (expanded) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
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
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (contact.role.isNotBlank()) {
                Text(
                    text = contact.role,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    letterSpacing = 0.5.sp
                )
            }
            Text(
                text = contact.phone,
                fontSize = 13.sp,
                color = Teal500,
                fontWeight = FontWeight.Medium
            )
        }
        IconButton(onClick = onCallClick) {
            Icon(
                Icons.Default.Call,
                contentDescription = "Call",
                tint = Teal500
            )
        }
    }
}

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
        containerColor = MaterialTheme.colorScheme.surface,
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
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Teal500,
                            cursorColor = Teal500
                        )
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CONTACTS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 3.sp
                        )
                        TextButton(onClick = { contacts = contacts + ContactFormState() }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Teal500
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add", color = Teal500, fontWeight = FontWeight.SemiBold)
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
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isBlank()) { nameError = true; return@TextButton }
                    val companyObj = Company(id = company?.id ?: 0, name = name.trim())
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
                Text("Save", fontWeight = FontWeight.Bold, color = Teal500)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
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
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Teal500,
                    cursorColor = Teal500
                )
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
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Teal500,
                    cursorColor = Teal500
                )
            )
            OutlinedTextField(
                value = contact.phone,
                onValueChange = { onContactChange(contact.copy(phone = it)) },
                label = { Text("Phone") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Teal500,
                    cursorColor = Teal500
                )
            )
        }
    }
}