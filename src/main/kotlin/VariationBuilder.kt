

import jdk.nashorn.tools.ShellFunctions.input
import jujutsu.tsne.barneshut.BHTSne
import jujutsu.tsne.barneshut.BarnesHutTSne
import jujutsu.tsne.barneshut.ParallelBHTsne
import jujutsu.utils.MatrixOps
import jujutsu.utils.MatrixUtils
import jujutsu.utils.TSneUtils
import sun.security.util.Length
//import voronoi.Point
import java.io.File
import java.util.Arrays

class VariationBuilder {

    init {

        var recepies : MutableList<BooleanArray> = ArrayList()

        // calculate variations
        val n = 6
        var i = 0

        println(Math.pow(2.0, n.toDouble()))
        while (i < Math.pow(2.0, n.toDouble())) {
            var bin = Integer.toBinaryString(i)

            while (bin.length < n)
                bin = "0$bin"

            val chars = bin.toCharArray()
            val boolArray = BooleanArray(n)

            for (j in chars.indices) {
                boolArray[j] = if (chars[j] == '0') true else false
            }

            recepies.add(boolArray)
            //println(Arrays.toString(boolArray))

            i++
            //println(i)
        }

        println(recepies.size)

        //println(Arrays.toString(recepies.get(0)))

        // exploit variations
        //executeVariation(recepies.last())
        recepies.forEachIndexed { index, booleans ->
            executeVariation(booleans)
            println(index.toString() + " of " + recepies.size)
        }
    }

    fun executeVariation(recipe: BooleanArray) {

        val initial_dims = 32
        val perplexity = 20.0

        val X = MatrixUtils.simpleRead2DMatrix(File("data/WDVP-Export.csv"), ";")

        var emptyDoubleArray = DoubleArray(32)
        emptyDoubleArray.forEachIndexed { index, d ->
            emptyDoubleArray.set(index, 0.0)
        }

        X.forEachIndexed { index, doubles ->

            var tempDoubles = doubles
            recipe.forEachIndexed { index, b ->

                if(b == true) { // on
                } else { // of
                    tempDoubles.set(index, 0.0)
                }
            }

            X.set(index, tempDoubles)
        }
        System.out.println(MatrixOps.doubleArrayToPrintString(X, ", ", 50,10));

        val tsne: BarnesHutTSne
        val parallel = true

        if (parallel) {
            tsne = ParallelBHTsne()
        } else {
            tsne = BHTSne()
        }

        val config = TSneUtils.buildConfig(X, 2, initial_dims, perplexity, 1000)
        val Y = tsne.tsne(config)

        var exportString = ""
        for ( i in 0 until Y.size) {
            exportString += Y.get(i).get(0).toString() + ";" + Y.get(i).get(1).toString() + "\n"
        }

        // the name
        var name = ""
        recipe.forEachIndexed { index, b ->
            if(b == true) {
                name += "1"
            } else {
                name += "0"
            }
            if(index < recipe.size-1) {
                name += "-"
            }
        }

        // save to file?
        File("data/storage/" + name + ".csv").writeText(exportString)
    }
}

fun main(args: Array<String>) {
    var variationBuilder = VariationBuilder()
}
