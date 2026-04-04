package com.grocery.manager.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import com.grocery.manager.ui.screens.CompanyScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.grocery.manager.ui.screens.AddEditProductScreen
import com.grocery.manager.ui.screens.HomeScreen
import com.grocery.manager.viewmodel.CategoryViewModel
import com.grocery.manager.viewmodel.CompanyViewModel
import com.grocery.manager.viewmodel.ProductViewModel

object Routes {
    const val HOME = "home"
    const val ADD_PRODUCT = "add_product"
    const val EDIT_PRODUCT = "edit_product/{productId}"
    const val COMPANIES = "companies"

    fun editProduct(productId: Int) = "edit_product/$productId"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val productViewModel: ProductViewModel = viewModel()
    val companyViewModel: CompanyViewModel = viewModel()
    val categoryViewModel: CategoryViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                productViewModel = productViewModel,
                onNavigateToAdd = { navController.navigate(Routes.ADD_PRODUCT) },
                onNavigateToEdit = { id -> navController.navigate(Routes.editProduct(id)) },
                onNavigateToCompanies = { navController.navigate(Routes.COMPANIES) }
            )
        }

        composable(Routes.ADD_PRODUCT) {
            AddEditProductScreen(
                productViewModel = productViewModel,
                companyViewModel = companyViewModel,
                categoryViewModel = categoryViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.EDIT_PRODUCT,
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId")
            AddEditProductScreen(
                productViewModel = productViewModel,
                companyViewModel = companyViewModel,
                categoryViewModel = categoryViewModel,
                productId = productId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.COMPANIES) {
            CompanyScreen(
                companyViewModel = companyViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}