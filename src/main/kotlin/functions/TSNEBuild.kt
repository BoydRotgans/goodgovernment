package functions

import jujutsu.tsne.barneshut.BHTSne
import jujutsu.tsne.barneshut.BarnesHutTSne
import jujutsu.tsne.barneshut.ParallelBHTsne
import jujutsu.utils.MatrixUtils
import jujutsu.utils.TSneUtils
import org.openrndr.draw.Drawer
import java.io.File

class TSNEBuild() {

    fun consume(recipe: BooleanArray): Array<out DoubleArray>? {

        val initial_dims = 32
        val perplexity = 15.0

        //val X = MatrixUtils.simpleRead2DMatrix(File("data/WDVP-Datasets-5.csv"), ";")
        val X = MatrixUtils.simpleRead2DMatrix(File("data/WDVP-Export-Normal.csv"), ";")
        //val X = MatrixUtils.simpleRead2DMatrix(File("data/WDVP-Export-Ranked.csv"), ";")

        var emptyDoubleArray = DoubleArray(32)
        emptyDoubleArray.forEachIndexed { index, d ->
            emptyDoubleArray.set(index, 0.0)
        }

        X.forEachIndexed { index, doubles ->

            var tempDoubles = doubles
            var countFalse = 0
            recipe.forEachIndexed { index, b ->
                if(b == true) { // on
                } else { // of
                    tempDoubles.set(index, 0.0)
                    countFalse++
                }
            }

            if(countFalse == doubles.size) {
                recipe.forEachIndexed { index, b ->
                    tempDoubles.set(index, 0.0001)
                }
            }

            X.set(index, tempDoubles)
        }

        val tsne: BarnesHutTSne
        val parallel = false

        if (parallel) {
            tsne = ParallelBHTsne()

        } else {
            tsne = BHTSne()
        }

        val config = TSneUtils.buildConfig(X, 2, initial_dims, perplexity, 1000)

        val Y = tsne.tsne(config)

        return Y
    }
}