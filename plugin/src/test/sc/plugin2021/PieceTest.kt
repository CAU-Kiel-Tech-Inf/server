package sc.plugin2021

import com.thoughtworks.xstream.XStream
import io.kotlintest.inspectors.forAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import org.opentest4j.AssertionFailedError
import sc.plugin2021.util.*


class PieceTest: StringSpec({
    "Test Piece initialisation" {
        for (pieceShape in PieceShape.shapes) {
            Piece(Color.GREEN, pieceShape.key, Rotation.NONE, false).shape shouldBe pieceShape.value.coordinates
        }

        Piece(Color.YELLOW, PieceShape.TETRO_O, Rotation.RIGHT, false).toString() shouldBe "YELLOW Piece TETRO_O:1 [0,0]"
        Piece(Color.RED,    PieceShape.PENTO_Y, Rotation.LEFT, false).toString() shouldBe "RED Piece PENTO_Y:3 [0,0]"
        Piece(Color.BLUE,   PieceShape.PENTO_P, Rotation.MIRROR, true).toString() shouldBe "BLUE Piece PENTO_P:2 (flipped) [0,0]"
        Piece(Color.GREEN,  PieceShape.TRIO_L, Rotation.NONE, true, Coordinates(5, 9)).toString() shouldBe "GREEN Piece TRIO_L:0 (flipped) [5,9]"
    }
    "Test PieceShape arithmetic" {
        setOf(Coordinates(1, 2), Coordinates(3, 2)).align() shouldBe setOf(Coordinates(0, 0), Coordinates(2, 0))

        for (pair in PieceShape.shapes) {
            val shape = pair.value.coordinates
            shape.rotate(Rotation.NONE) shouldBe shape
            shape.rotate(Rotation.RIGHT).rotate(Rotation.RIGHT) shouldBe shape.rotate(Rotation.MIRROR)
            shape.rotate(Rotation.MIRROR).rotate(Rotation.MIRROR) shouldBe shape
            shape.rotate(Rotation.LEFT) shouldBe shape.rotate(Rotation.MIRROR).rotate(Rotation.RIGHT)
            shape.flip(false) shouldBe shape
            shape.flip(true).flip() shouldBe shape
        }
    }
    "Test Set transformation arithmetic (PENTO_W)" {
        val TID = listOf("NN", "RN", "MN", "LN", "NY", "RY", "MY", "LY")
        val shape = PieceShape.PENTO_W
        val shapes = listOf(
                setOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(1, 2), Coordinates(2, 2)),
                setOf(Coordinates(0, 2), Coordinates(0, 1), Coordinates(1, 1), Coordinates(1, 0), Coordinates(2, 0)),
                setOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(1, 1), Coordinates(2, 1), Coordinates(2, 2)),
                setOf(Coordinates(0, 2), Coordinates(1, 2), Coordinates(1, 1), Coordinates(2, 1), Coordinates(2, 0))
        )
        val SHOULD = (TID zip (shapes + shapes.asReversed())).toMap()
        val transformations = (TID zip (
                (Rotation.values() zip List(Rotation.values().size) {false}) +
                        (Rotation.values() zip List(Rotation.values().size) {true})
                )).toMap()
        val IS = transformations.map {
            it.key to shape.transform(it.value.first, it.value.second)
        }.toMap()
    
        TID.forEach {
            try {
                IS[it] shouldBe SHOULD[it]
            } catch (e: AssertionFailedError) {
                println("Expected:  $it  Actual:")
                printShapes(SHOULD.getValue(it), IS.getValue(it))
                throw e
            }
        }
    }
    "Test Set transformation arithmetic (PENTO_R)" {
        val shape = PieceShape.PENTO_R
        val TID = listOf("NN", "RN", "MN", "LN", "NY", "RY", "MY", "LY")
        val SHOULD = (TID zip listOf(
                setOf(Coordinates(0, 1), Coordinates(1, 2), Coordinates(1, 1), Coordinates(2, 1), Coordinates(2, 0)),
                setOf(Coordinates(0, 1), Coordinates(1, 0), Coordinates(1, 1), Coordinates(1, 2), Coordinates(2, 2)),
                setOf(Coordinates(0, 2), Coordinates(0, 1), Coordinates(1, 1), Coordinates(1, 0), Coordinates(2, 1)),
                setOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(1, 1), Coordinates(1, 2), Coordinates(2, 1)),
                setOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(1, 2), Coordinates(2, 1)),
                setOf(Coordinates(0, 2), Coordinates(1, 2), Coordinates(1, 1), Coordinates(1, 0), Coordinates(2, 1)),
                setOf(Coordinates(0, 1), Coordinates(1, 0), Coordinates(1, 1), Coordinates(2, 1), Coordinates(2, 2)),
                setOf(Coordinates(0, 1), Coordinates(1, 2), Coordinates(1, 1), Coordinates(1, 0), Coordinates(2, 0))
        )).toMap()
        val transformations = (TID zip (
                (Rotation.values() zip List(Rotation.values().size) {false}) +
                (Rotation.values() zip List(Rotation.values().size) {true})
        )).toMap()
        val IS = transformations.map {
            it.key to shape.transform(it.value.first, it.value.second)
        }.toMap()
        
        TID.forEach {
            try {
                IS[it] shouldBe SHOULD[it]
            } catch (e: AssertionFailedError) {
                println("Expected:  $it  Actual:")
                printShapes(SHOULD.getValue(it), IS.getValue(it))
                throw e
            }
        }
    }
    "Test Set transformation arithmetic (TETRO_L)" {
        val shape = PieceShape.TETRO_L
        val TID = listOf("NN", "RN", "MN", "LN", "NY", "RY", "MY", "LY")
        val SHOULD = (TID zip listOf(
                setOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(0, 2), Coordinates(1, 2)),
                setOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(1, 0), Coordinates(2, 0)),
                setOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(1, 1), Coordinates(1, 2)),
                setOf(Coordinates(0, 1), Coordinates(1, 1), Coordinates(2, 1), Coordinates(2, 0)),
                setOf(Coordinates(1, 0), Coordinates(1, 1), Coordinates(1, 2), Coordinates(0, 2)),
                setOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(2, 0), Coordinates(2, 1)),
                setOf(Coordinates(1, 0), Coordinates(0, 0), Coordinates(0, 1), Coordinates(0, 2)),
                setOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(2, 1))
        )).toMap()
        val transformations = (TID zip (
                (Rotation.values() zip List(Rotation.values().size) {false}) +
                        (Rotation.values() zip List(Rotation.values().size) {true})
                )).toMap()
        val IS = transformations.map {
            it.key to shape.transform(it.value.first, it.value.second)
        }.toMap()
        
        TID.forEach {
            try {
                IS[it] shouldBe SHOULD[it]
            } catch (e: AssertionFailedError) {
                println("Expected:  $it  Actual:")
                printShapes(SHOULD.getValue(it), IS.getValue(it))
                throw e
            }
        }
    }
    "Piece coordination calculation" {
        val position = Coordinates(2, 2)
        val coordinates = setOf(Coordinates(2, 3), Coordinates(3, 3), Coordinates(3, 2))
        val piece = Piece(Color.RED, PieceShape.TRIO_L, Rotation.NONE, true, position)
    
        piece.shape shouldBe PieceShape.TRIO_L.coordinates.flip()
        piece.coordinates shouldBe coordinates
    }
    "XML conversion" {
        val pieces = listOf(
                Piece(Color.YELLOW, PieceShape.TETRO_O, Rotation.RIGHT, false),
                Piece(Color.RED,    PieceShape.PENTO_Y, Rotation.LEFT, false),
                Piece(Color.BLUE,   PieceShape.PENTO_P, Rotation.MIRROR, true),
                Piece(Color.GREEN,  PieceShape.TRIO_L, Rotation.NONE, true, Coordinates(5, 9))
        )
    
        Configuration.xStream.toXML(pieces[0]) shouldBe """
            <piece color="YELLOW" kind="TETRO_O" rotation="RIGHT" isFlipped="false">
              <position x="0" y="0"/>
            </piece>
        """.trimIndent()
        Configuration.xStream.toXML(pieces[1]) shouldBe """
            <piece color="RED" kind="PENTO_Y" rotation="LEFT" isFlipped="false">
              <position x="0" y="0"/>
            </piece>
        """.trimIndent()
        Configuration.xStream.toXML(pieces[2]) shouldBe """
            <piece color="BLUE" kind="PENTO_P" rotation="MIRROR" isFlipped="true">
              <position x="0" y="0"/>
            </piece>
        """.trimIndent()
        Configuration.xStream.toXML(pieces[3]) shouldBe """
            <piece color="GREEN" kind="TRIO_L" rotation="NONE" isFlipped="true">
              <position x="5" y="9"/>
            </piece>
        """.trimIndent()

        pieces.forEach{
            val xml = Configuration.xStream.toXML(it)
            val converted = Configuration.xStream.fromXML(xml) as Piece
            converted.toString() shouldBe it.toString()
            converted shouldBe it
            println("Expected: ${it.coordinates} - Actual: ${converted.coordinates}")
        }
    }
    "Piece transformation calculation" {
    
    }
})