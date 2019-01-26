import org.openrndr.math.map
import java.io.File
import kotlin.math.exp

class DataMachineRanking {

    fun parse() {

        var countriesList = File("data/WDVP-Datasets-Original.csv").readLines()
        val orderColumn = File("data/invertColumn.txt").readLines()

        var columnCount = countriesList.get(0).split(";").size
        //var indexOfArrays = mapOf<Int, MutableList<Double>>()
        val seqList: MutableList<MutableList<Double>> = ArrayList()
        val seqListNormal: MutableList<MutableList<Double>> = ArrayList()
        val seqListSorted: MutableList<MutableList<Double>> = ArrayList()
        val seqListRanked: MutableList<MutableList<Double>> = ArrayList()

        for (i in 0 until columnCount) {
            seqList.add(i, mutableListOf())
        }

        for (i in 0 until columnCount) {
            seqListNormal.add(i, mutableListOf())
        }

        // place
        countriesList.forEachIndexed { indexCountries, s ->
            var vars = s.split(";")
            vars.forEachIndexed { indexVars, x ->
                var templist = seqList.get(indexVars)
                //println(indexCountries)
                //println(vars.get(indexVars).toDouble())

                var value = vars.get(indexVars).toDouble()
                if(value.equals(-999.0)) { // CORRECT FOR MISSING DATA
                    value = 0.0
                }

                templist.add(value)
                seqList[indexVars] = templist
            }
        }


        // normal
        seqList.forEachIndexed { indexRoot, mutableList ->

            val min = mutableList.min()!!.toDouble()
            val max = mutableList.max()!!.toDouble()

            println("Column:" + indexRoot + "; min: " + min + "; max: " + max)

            mutableList.forEachIndexed { indexVars, d ->
                val tempList = seqListNormal.get(indexRoot)

                var normal = map(min, max, -1.0, 1.0, d) // normal order
                if(orderColumn[indexRoot].toInt() == -1) {
                    normal = map(min, max, 1.0, -1.0, d) // inverted order
                }

                tempList.add(normal)
                seqListNormal[indexRoot] = tempList
            }

        }

        // sort
        seqListNormal.forEachIndexed { indexRoot, mutableList ->

            var sortedList = mutableList.sortedWith(compareBy { -it })


            sortedList.forEachIndexed { index, d ->
               // println(d)
            }
            seqListSorted.add(sortedList as MutableList<Double>)

        }

        // build ranking
//        seqList.forEachIndexed { indexRoot, mutableList ->
//
//            mutableList
//
//            seqListSorted.get(indexRoot).forEachIndexed { index, d ->
//
//            }
//
//        }


        var exportString = ""
        for(r in 0 until countriesList.size) { // rows
            if(1==1) {
                for (c in 0 until columnCount) { // column


                    var value = seqListNormal.get(c).get(r)
                    println("check for " + value)

                    var rank = -999
                    while (rank == -999) {
                        seqListSorted.get(c).forEachIndexed { index, d ->
                            //println(d)
                            //println(d)

                            if (value == d) {
                                rank = index
                            }
                        }
                    }

                    println("ranked:" + rank)
                    exportString += rank
                    exportString += ";"


                }
                exportString += "\n"
            }
        }

        println(exportString)
        File("data/WDVP-Export-Ranked.csv").writeText(exportString)

    }



}

fun main(args: Array<String>) {

    var dataMachine = DataMachineRanking()
    dataMachine.parse()

}