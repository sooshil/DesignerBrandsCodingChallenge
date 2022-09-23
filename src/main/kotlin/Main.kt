import java.io.File

fun main(args: Array<String>) {
    if (args.isEmpty().not()) {
        val filePath = args[0]
        val lines = getFileContents(filePath)
        val products = mutableListOf<Product>()
        lines?.forEachIndexed { index, line ->
            if (index > 2) {
                val list = line.split(",")
                if (list.isEmpty().not() && list[3].toInt() > 2) {
                    val type = when {
                        list[1].toDouble() == list[2].toDouble() -> Type.NORMAL
                        list[1].toDouble() > list[2].toDouble() -> Type.CLEARANCE
                        else -> Type.CART
                    }
                    products.add(
                        Product(
                            type = type,
                            normalPrice = list[1].toDouble(),
                            clearancePrice = list[2].toDouble(),
                            quantity = list[3].toInt(),
                            list[4] == "true"
                        )
                    )
                }
            }
        }
        generateReport(products)
    } else {
        println("Please enter file name to read the data from.")
    }
}

fun generateReport(products: MutableList<Product>) {
    val normalPriceCount = Pair(Type.NORMAL, products.count { it.type == Type.NORMAL })
    val clearancePriceCount = Pair(Type.CLEARANCE, products.count { it.type == Type.CLEARANCE })
    val cartPriceCount = Pair(Type.CART, products.count { it.isPriceHidden })

    val normalPriceMin = products.filter { it.type == Type.NORMAL }.minOf { it.normalPrice }
    val normalPriceMax = products.filter { it.type == Type.NORMAL }.maxOf { it.normalPrice }
    val clearancePriceMin = products.filter { it.type == Type.CLEARANCE }.minOf { it.clearancePrice }
    val clearancePriceMax = products.filter { it.type == Type.CLEARANCE }.maxOf { it.clearancePrice }

    val normalPriceRange = when (normalPriceMax) {
        normalPriceMin -> "@ $$normalPriceMin"
        else -> "@ $$normalPriceMin-$$normalPriceMax"
    }

    val clearancePriceRange = when (clearancePriceMax) {
        clearancePriceMin -> "@ $$clearancePriceMin"
        else -> "@ $$clearancePriceMin-$$clearancePriceMax"
    }

    val sortedCounts = listOf(normalPriceCount, clearancePriceCount, cartPriceCount).sortedByDescending { it.second }
    sortedCounts.forEach {
        when(it.first) {
            Type.NORMAL -> println("${Type.NORMAL.value()}: ${normalPriceCount.second} ${getPluralText(normalPriceCount.second)} $normalPriceRange")
            Type.CLEARANCE -> println("${Type.CLEARANCE.value()}: ${clearancePriceCount.second} ${getPluralText(clearancePriceCount.second)} $clearancePriceRange")
            else -> println("${Type.CART.value()}: ${cartPriceCount.second} ${getPluralText(cartPriceCount.second)}")
        }
    }
}

fun getPluralText(counts: Int) = when(counts) {
    1 -> "product"
    else -> "products"
}

fun getFileContents(filePath: String): List<String>? {
    return try {
        File(filePath).readLines()
    } catch (ex: java.lang.Exception) {
        println("File with the specified file name not found.")
        null
    }
}

enum class Type(private val displayName: String) {
    NORMAL("Normal Price"),
    CLEARANCE("Clearance Price"),
    CART("Price In Cart");

    fun value() = displayName
}

data class Product(
    val type: Type,
    val normalPrice: Double,
    val clearancePrice: Double,
    val quantity: Int,
    val isPriceHidden: Boolean
)