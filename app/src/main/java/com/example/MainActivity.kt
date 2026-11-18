package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.GolfCourse
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.GolfCourse
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.i18n.stringsFor
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.RoundSetupScreen
import com.example.ui.screens.ScorecardScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.FairwayCardTint
import com.example.ui.theme.FairwayDeepGreen
import com.example.ui.theme.InkMuted
import com.example.ui.theme.JaypeeGreensTheme
import com.example.ui.theme.ScorecardWhite
import com.example.ui.viewmodel.GolfViewModel

object AppRoutes {
  const val SETUP = "round_setup"
  const val SCORECARD = "scorecard"
  const val HISTORY = "history"
  const val SETTINGS = "settings"
}

private data class NavDestinationItem(
  val route: String,
  val label: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val testTag: String,
)

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      JaypeeGreensApp()
    }
  }
}

@Composable
fun JaypeeGreensApp(
  viewModel: GolfViewModel = viewModel(),
) {
  val language by viewModel.language.collectAsStateWithLifecycle()
  val setupState by viewModel.setupState.collectAsStateWithLifecycle()
  val recentPlayers by viewModel.recentPlayers.collectAsStateWithLifecycle()
  val courseHoles by viewModel.courseHoles.collectAsStateWithLifecycle()
  val activeRound by viewModel.activeRound.collectAsStateWithLifecycle()
  val activeRoundId by viewModel.activeRoundId.collectAsStateWithLifecycle()
  val scorecardSelection by viewModel.scorecardSelection.collectAsStateWithLifecycle()
  val roundHistory by viewModel.roundHistory.collectAsStateWithLifecycle()

  val strings = stringsFor(language)

  JaypeeGreensTheme {
    CompositionLocalProvider(LocalAppStrings provides strings) {
      val navController = rememberNavController()
      val backStackEntry by navController.currentBackStackEntryAsState()
      val currentRoute = backStackEntry?.destination?.route ?: AppRoutes.SETUP

      val navItems = listOf(
        NavDestinationItem(
          route = AppRoutes.SETUP,
          label = strings.navNewRound,
          selectedIcon = Icons.Filled.AddCircle,
          unselectedIcon = Icons.Outlined.AddCircleOutline,
          testTag = "nav_setup",
        ),
        NavDestinationItem(
          route = AppRoutes.SCORECARD,
          label = strings.navScorecard,
          selectedIcon = Icons.Filled.GolfCourse,
          unselectedIcon = Icons.Outlined.GolfCourse,
          testTag = "nav_scorecard",
        ),
        NavDestinationItem(
          route = AppRoutes.HISTORY,
          label = strings.navHistory,
          selectedIcon = Icons.Filled.History,
          unselectedIcon = Icons.Outlined.History,
          testTag = "nav_history",
        ),
        NavDestinationItem(
          route = AppRoutes.SETTINGS,
          label = strings.navSettings,
          selectedIcon = Icons.Filled.Settings,
          unselectedIcon = Icons.Outlined.Settings,
          testTag = "nav_settings",
        ),
      )

      fun navigateTo(route: String) {
        navController.navigate(route) {
          popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
          }
          launchSingleTop = true
          restoreState = true
        }
      }

      BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavRail = maxWidth >= 600.dp

        Scaffold(
          contentWindowInsets = WindowInsets.safeDrawing,
          bottomBar = {
            if (!useNavRail) {
              NavigationBar(
                containerColor = ScorecardWhite,
                tonalElevation = 4.dp,
              ) {
                navItems.forEach { item ->
                  val selected = currentRoute == item.route
                  NavigationBarItem(
                    selected = selected,
                    onClick = { navigateTo(item.route) },
                    icon = {
                      Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                      )
                    },
                    label = {
                      Text(
                        text = item.label,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                      )
                    },
                    colors = NavigationBarItemDefaults.colors(
                      selectedIconColor = FairwayDeepGreen,
                      selectedTextColor = FairwayDeepGreen,
                      indicatorColor = FairwayCardTint,
                      unselectedIconColor = InkMuted,
                      unselectedTextColor = InkMuted,
                    ),
                    modifier = Modifier.testTag(item.testTag),
                  )
                }
              }
            }
          },
        ) { innerPadding ->
          Row(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding),
          ) {
            if (useNavRail) {
              NavigationRail(
                containerColor = ScorecardWhite,
              ) {
                navItems.forEach { item ->
                  val selected = currentRoute == item.route
                  NavigationRailItem(
                    selected = selected,
                    onClick = { navigateTo(item.route) },
                    icon = {
                      Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                      )
                    },
                    label = {
                      Text(
                        text = item.label,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                      )
                    },
                    colors = NavigationRailItemDefaults.colors(
                      selectedIconColor = FairwayDeepGreen,
                      selectedTextColor = FairwayDeepGreen,
                      indicatorColor = FairwayCardTint,
                      unselectedIconColor = InkMuted,
                      unselectedTextColor = InkMuted,
                    ),
                    modifier = Modifier.testTag(item.testTag),
                  )
                }
              }
            }

            NavHost(
              navController = navController,
              startDestination = AppRoutes.SETUP,
              modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            ) {
              composable(AppRoutes.SETUP) {
                RoundSetupScreen(
                  setupState = setupState,
                  recentPlayers = recentPlayers,
                  totalCoursePar = courseHoles.sumOf { it.par },
                  onDateSelected = viewModel::setSetupDate,
                  onTeeSelected = viewModel::setSetupTee,
                  onAddPlayer = viewModel::addSetupPlayer,
                  onRemovePlayer = viewModel::removeSetupPlayer,
                  onPlayerNameChange = viewModel::updateSetupPlayerName,
                  onPlayerHandicapChange = viewModel::updateSetupPlayerHandicap,
                  onStepHandicap = viewModel::stepSetupPlayerHandicap,
                  onSelectRecentPlayer = viewModel::applyRecentPlayer,
                  onDeleteRecentPlayer = viewModel::removeRecentPlayer,
                  onStartRound = {
                    viewModel.startNewRound(strings.playerDefaultPrefix) {
                      navigateTo(AppRoutes.SCORECARD)
                    }
                  },
                )
              }

              composable(AppRoutes.SCORECARD) {
                ScorecardScreen(
                  activeRound = activeRound,
                  selection = scorecardSelection,
                  onSelectNineTab = viewModel::selectNineTab,
                  onSelectCell = viewModel::selectCellForInput,
                  onSelectHole = viewModel::selectHoleForInput,
                  onSelectPlayer = viewModel::selectPlayerForInput,
                  onToggleInputPanel = viewModel::setInputPanelOpen,
                  onRecordScore = { playerId, holeNum, rel ->
                    activeRound?.let { round ->
                      viewModel.recordScoreAndAdvance(round, playerId, holeNum, rel)
                    }
                  },
                  onClearScore = { playerId, holeNum ->
                    activeRound?.let { round ->
                      viewModel.clearScore(round.roundId, playerId, holeNum)
                    }
                  },
                  onNextPlayer = { currentPlayerId ->
                    activeRound?.let { round ->
                      viewModel.advanceToNextPlayer(round, currentPlayerId)
                    }
                  },
                  onNextHole = viewModel::advanceToNextHole,
                  onGoToNewRound = { navigateTo(AppRoutes.SETUP) },
                  onGoToHistory = { navigateTo(AppRoutes.HISTORY) },
                )
              }

              composable(AppRoutes.HISTORY) {
                HistoryScreen(
                  rounds = roundHistory,
                  activeRoundId = activeRoundId,
                  onOpenRound = { roundId ->
                    viewModel.openRound(roundId) {
                      navigateTo(AppRoutes.SCORECARD)
                    }
                  },
                  onDeleteRound = viewModel::deleteRound,
                )
              }

              composable(AppRoutes.SETTINGS) {
                SettingsScreen(
                  language = language,
                  holes = courseHoles,
                  onLanguageChange = viewModel::setLanguage,
                  onUpdatePar = viewModel::updateHolePar,
                  onUpdateStrokeIndex = viewModel::updateHoleStrokeIndex,
                  onResetDefaults = viewModel::resetCourseDefaults,
                )
              }
            }
          }
        }
      }
    }
  }
}
