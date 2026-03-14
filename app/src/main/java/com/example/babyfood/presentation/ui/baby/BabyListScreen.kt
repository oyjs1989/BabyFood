package com.example.babyfood.presentation.ui.baby

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import android.widget.Toast
import com.example.babyfood.presentation.theme.ButtonPrimary
import com.example.babyfood.presentation.ui.common.AppScaffold

import androidx.compose.ui.res.stringResource
import com.example.babyfood.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BabyListScreen(
    onNavigateToAdd: () -> Unit = {},
    onNavigateToDetail: (Long) -> Unit = {},
    onAfterSetAsCurrentBaby: () -> Unit = {},
    viewModel: BabyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    AppScaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = ButtonPrimary,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.baby_add_title)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(R.string.loading))
                }
            } else if (uiState.babies.isEmpty()) {
                com.example.babyfood.presentation.theme.EmptyState(
                    icon = Icons.Default.ChildCare,
                    title = stringResource(R.string.baby_empty_list),
                    description = stringResource(R.string.baby_empty_description)
                )
            } else {
                val context = LocalContext.current
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.babies) { baby ->
                        val isSelected = baby.id == uiState.selectedBabyId
                        val switchSuccessMsg = stringResource(R.string.baby_switch_success, baby.name)
                        BabyCard(
                            baby = baby,
                            isSelected = isSelected,
                            onClick = { onNavigateToDetail(baby.id) },
                            onSetAsCurrent = {
                                viewModel.setAsCurrentBaby(baby)
                                Toast.makeText(
                                    context,
                                    switchSuccessMsg,
                                    Toast.LENGTH_SHORT
                                ).show()
                                onAfterSetAsCurrentBaby()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BabyCard(
    baby: com.example.babyfood.domain.model.Baby,
    isSelected: Boolean,
    onClick: () -> Unit = {},
    onSetAsCurrent: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier
                }
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = if (isSelected) {
            null
        } else {
            androidx.compose.foundation.BorderStroke(
                0.5.dp,
                MaterialTheme.colorScheme.outline
            )
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onClick)
            ) {
                Text(
                    text = baby.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.baby_age_format, baby.ageInMonths),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(R.string.baby_birthday_format, baby.birthDate),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                TextButton(onClick = onSetAsCurrent) {
                    Text(stringResource(R.string.baby_set_current))
                }
            }
        }
    }
}