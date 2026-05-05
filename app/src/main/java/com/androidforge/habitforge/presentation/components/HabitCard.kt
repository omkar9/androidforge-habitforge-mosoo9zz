package com.androidforge.habitforge.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidforge.habitforge.R
import com.androidforge.habitforge.domain.model.Habit
import com.androidforge.habitforge.presentation.theme.HabitForgeTheme
import com.androidforge.habitforge.presentation.theme.Primary
import com.androidforge.habitforge.presentation.theme.Error
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HabitCard(
    habit: Habit,
    onHabitClick: (Habit) -> Unit,
    onMarkComplete: (Habit) -> Unit,
    onMarkSkipped: (Habit) -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBackgroundColor = when {
        habit.isCompletedToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        habit.isSkippedToday -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
        else -> MaterialTheme.colorScheme.surface
    }

    val cardBorderColor = when {
        habit.isCompletedToday -> MaterialTheme.colorScheme.primary
        habit.isSkippedToday -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .combinedClickable(
                onClick = { onHabitClick(habit) },
                onLongClick = { /* Maybe show more options or edit directly */ }
            ),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        border = BorderStroke(1.dp, cardBorderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = habit.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (habit.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = habit.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.current_streak),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${habit.currentStreak} days",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row {
                    if (!habit.isCompletedToday && !habit.isSkippedToday) {
                        AppToolbar.AppButton(
                            onClick = { onMarkSkipped(habit) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.mark_skipped),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.mark_skipped))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        AppToolbar.AppButton(
                            onClick = { onMarkComplete(habit) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = stringResource(R.string.mark_completed),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.mark_completed))
                        }
                    } else {
                        AnimatedVisibility(
                            visible = habit.isCompletedToday,
                            enter = fadeIn(animationSpec = tween(500)) + androidx.compose.animation.scaleIn(animationSpec = tween(500)),
                            exit = fadeOut(animationSpec = tween(500)) + androidx.compose.animation.scaleOut(animationSpec = tween(500))
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(R.string.habit_completed_today),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        AnimatedVisibility(
                            visible = habit.isSkippedToday,
                            enter = fadeIn(animationSpec = tween(500)) + androidx.compose.animation.scaleIn(animationSpec = tween(500)),
                            exit = fadeOut(animationSpec = tween(500)) + androidx.compose.animation.scaleOut(animationSpec = tween(500))
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(R.string.habit_skipped_today),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun HabitCardPreview() {
    HabitForgeTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            HabitCard(
                habit = Habit(1, "Drink Water", "Stay hydrated", LocalDate.now(), 5, 10, true, false),
                onHabitClick = {},
                onMarkComplete = {},
                onMarkSkipped = {},
                modifier = Modifier.padding(8.dp)
            )
            HabitCard(
                habit = Habit(2, "Read Book", "Read for 30 minutes", LocalDate.now(), 2, 5, false, false),
                onHabitClick = {},
                onMarkComplete = {},
                onMarkSkipped = {},
                modifier = Modifier.padding(8.dp)
            )
            HabitCard(
                habit = Habit(3, "Exercise", "Go for a run", LocalDate.now(), 0, 0, false, true),
                onHabitClick = {},
                onMarkComplete = {},
                onMarkSkipped = {},
                modifier = Modifier.padding(8.dp)
            )
            HabitCard(
                habit = Habit(4, "Meditate", "10 minutes mindfulness", LocalDate.now(), 7, 15, true, false),
                onHabitClick = {},
                onMarkComplete = {},
                onMarkSkipped = {},
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}