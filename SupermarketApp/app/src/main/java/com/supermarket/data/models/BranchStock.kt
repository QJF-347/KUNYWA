package com.supermarket.data.models

import com.google.gson.annotations.SerializedName

data class BranchStock(
    @SerializedName("branchId")
    val branchId: Int,
    @SerializedName("branchName")
    val branchName: String,
    @SerializedName("stocks")
    val stocks: List<Stock>
)

data class AllBranchesStock(
    @SerializedName("branches")
    val branches: List<BranchStock>
)
