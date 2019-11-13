package de.tuberlin.sese.swtpp.gameserver.test.deathstacks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.tuberlin.sese.swtpp.gameserver.control.GameController;
import de.tuberlin.sese.swtpp.gameserver.model.Player;
import de.tuberlin.sese.swtpp.gameserver.model.User;
import de.tuberlin.sese.swtpp.gameserver.model.deathstacks.DeathStacksGame;

public class TryMoveTest {

	User user1 = new User("Alice", "alice");
	User user2 = new User("Bob", "bob");
	
	Player redPlayer = null;
	Player bluePlayer = null;
	DeathStacksGame game = null;
	GameController controller;
	
	String gameType ="deathstacks";
	
	@Before
	public void setUp() throws Exception {
		controller = GameController.getInstance();
		controller.clear();
		
		int gameID = controller.startGame(user1, "", gameType);
		
		game = (DeathStacksGame) controller.getGame(gameID);
		redPlayer = game.getPlayer(user1);

	}
	
	public void startGame(String initialBoard, boolean redNext) {
		controller.joinGame(user2, gameType);		
		bluePlayer = game.getPlayer(user2);
		
		game.setBoard(initialBoard);
		game.setNextPlayer(redNext? redPlayer:bluePlayer);
	}
	
	public void assertMove(String move, boolean red, boolean expectedResult) {
		if (red)
			assertEquals(expectedResult, game.tryMove(move, redPlayer));
		else 
			assertEquals(expectedResult,game.tryMove(move, bluePlayer));
	}
	
	public void assertGameState(String expectedBoard, boolean redNext, boolean finished, boolean draw, boolean redWon) {
		String board = game.getBoard();
				System.out.println(board);
		assertEquals(expectedBoard,board);
		assertEquals(finished, game.isFinished());
		if (!game.isFinished()) {
			assertEquals(redNext, game.isRedNext());
		} else {
			assertEquals(draw, game.isDraw());
			if (!draw) {
				assertEquals(redWon, redPlayer.isWinner());
				assertEquals(!redWon, bluePlayer.isWinner());
			}
		}
	}

	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 *******************************************/
	
//TODO ANMERKUNGEN
//	Ich habe bei isMoveValid spielfeld[start.y][start.x].length()<tooTall  aus der einen bedingung wegen Redundanz gelöscht.
//	Diese Bedingung wird bereits von der Methode checkTooTall() geprüft, womit die Bedingung verundet wurde.
	
//	Beim switch in SetBoard habe ich den Default wert gelöscht, da dieser nicht erreichbar war.
	
//	Bei stringToCoordinate habe ich aus "if (coord.x==dimX || coord.x<0 || coord.y==dimY || coord.y<0) return null;"
//	"if (coord.x>=dimX  || coord.y>=dimY  ) return null;" gemacht, da die coords nie negativ werden können (String wird größer)		
	
//	bei moveDir habe ich die Bedingung if dir <0 || dir > 7 gelöscht, da dieser Fall von der von der for-schleife aus isMoveValid ausgeschlossen wird.
	
//	bei ownedBy habe ich die Bedingung "if(spielfeld[y][x].length()>=0)" gelöscht, da an diesem Punkt spielfeld[x][y].length nicht >= 0 sein kann.
//	Denn in jedem Aufruf von ownedBy wird ownedBy mit if(spielfeld[y][x].length()>=0) verundet.


//	in ownedBy : 
//	aus if ( (spielfeld[y][x].charAt(0)=='r' && player==redPlayer) ||
//		(spielfeld[y][x].charAt(0)=='b' && player==bluePlayer) ) {	
//	
//	wurde if ( (spielfeld[y][x].charAt(0)=='r' && player==redPlayer) ||
//		(player==bluePlayer) ) {
//	
//	Begründung: spielfeld[y][x].charAt(0)=='b' wird wegen der Veroderung immer true sein oder geskippt werden.
	
//	@Test
	//example
//	public void Test2() {
//		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
//		assertMove("d6-1-d4",true,false);
//		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true,false,false,false);
//	}
	
	
	
	
	
//	TODO: Neue Anmerkungen
//	Bei isMoveValid wird die Bedingung if (spielfeld[start.y][start.x].length()< steps) return false; nicht benötigt, wie man am Test @TestNotEnoughStones sehen kann.
//	Begründung: Wenn man die Bedingung vor if (spielfeld[start.y][start.x].length()==0)return false;, alsso vor Zeile 334 setzt, wird die Bedingung erfüllt, aber dafür die darunter nicht. Die genaue abhängigkeit kann ich nicht erklären, aber "Test leer" scheint "nicht genug steine zu überdecken."

	//TODO: implement test cases of same kind as example here

	//ivalid formats
	

	
	//TODO Ich schaffe es nicht einen Move zu erstellen, sodass im Code bei TryMove if (nSteps<1) return false; gecovered wird.
	@Test
	//covers stringToCoordinate
	public void TestBoundaries() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		
		assertMove("aa6-1-a5",true,false);	
		
		assertMove("a5-1-", true, false);
		
		assertMove("a7-1-a5",true,false);
		assertMove("g7-1-a5",true,false);
		
		assertMove(" 2-1-a5",true,false); //coord.x<0
		assertMove("b -1-a5",true,false); //coord.y<0
		
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
	}
	
	@Test
	public void TestBoundaries2() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",false);
		assertMove("a6-1-a5", true, false);
	}
	
	//TODO Bei Leerzeichen im movestring scheint das Format nicht als falsch erkannt zu werden
	@Test
	public void TestBoundaries3() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("  -1-a5",true,false);
		assertMove("a1-0-a1",true,false);
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
	}
	
	
	//tootall

	@Test
	public void TestTooTall() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bbbbbb,bb,bb,bb,bb,bb",true);
		assertFalse(game.checkTooTall());
	}
	
	
	@Test
	//covers ownedBy and checkTooTall
	public void TestRedTooTall() {
		startGame("rrrrr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertTrue(game.checkTooTall());
		assertTrue(game.ownedBy(0, 5, redPlayer));	
	}
	
	@Test
	public void TestBlueTooTall() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bbbbbb,bb,bb,bb,bb,bb",false);
		assertTrue(game.checkTooTall());
		assertTrue(game.ownedBy(0, 0, bluePlayer));
	}
	
	@Test
	public void MoveTooTall() {
		startGame("rrrrr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("a6-2-a4",true,true);
		assertGameState("rrr,rr,rr,rr,rr,rr/,,,,,/rr,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb", false, false, false, false);
	}
	
	@Test
	public void DontMoveTooTall() {
		startGame("rrrrr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("b6-2-b4",true,false);
		assertGameState("rrrrr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb", true, false, false, false);
	}
	
	@Test
	public void StillTooTall() {
		startGame("rrrrrrrr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("a6-2-a4",true,false);
		assertGameState("rrrrrrrr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb", true, false, false, false);
	}
	//ownedby
	
	@Test
	public void TestownedBy() {
		startGame("rrrrr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",false);
		assertFalse(game.ownedBy(0, 5, bluePlayer));
	}
	
	
	
	//testing all directions with and without touching borders
	
	@Test
	public void TestMoveDirDown() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("a6-1-a5",true,true);
		assertGameState("r,rr,rr,rr,rr,rr/r,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",false,false,false,false);
	}	
	
	@Test
	public void TestLeftWall() {
		startGame("rr,rr,rr,rr,rr,rr/,rrr,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("b5-3-c5",true,true);
		assertGameState("rr,rr,rr,rr,rr,rr/,,rrr,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",false,false,false,false);
	}
	
	@Test 
	//schräg nach oben gegen die linke Wand
	public void TestUpLeftWall() {
		startGame("rr,rr,rr,rr,rr,rr/"
				+ ",,,,,/"
				+ ",,,,,/"
				+ ",,,,,/"
				+ ",rrr,,,,/"
				+ "b,,,,,",true);
		assertMove("b2-3-c5",true,true);
		assertGameState("rr,rr,rr,rr,rr,rr/"
				+ 		",,rrr,,,/"
				+ 		",,,,,/"
				+ 		",,,,,/"
				+ 		",,,,,/"
				+ 		"b,,,,,",false,false,false,false);
		}

	@Test
	//schräg nach unten gegen die linke Wand
	public void TestDownLeftWall() {
		startGame("rr,rr,rr,rr,rr,rr/"
				+ ",rrr,,,,/"
				+ ",,,,,/"
				+ ",,,,,/"
				+ ",,,,,/"
				+ "b,,,,,",true);
		assertMove("b5-3-c2",true,true);
		assertGameState("rr,rr,rr,rr,rr,rr/"
				+ 		",,,,,/"
				+ 		",,,,,/"
				+ 		",,,,,/"
				+ 		",,rrr,,,/"
				+ 		"b,,,,,",false,false,false,false);
	}
	
	@Test
	public void TestRightWall() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,rrr,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("e5-3-d5",true,true);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,rrr,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",false,false,false,false);
	}
	
	@Test
	//schräg nach oben gegen die rechte Wand
	public void TestUpRightWall() {
		startGame("rr,rr,rr,rr,rr,rr/"
				+ ",,,,,/"
				+ ",,,,,/"
				+ ",,,,,/"
				+ ",,,,,/"
				+ "b,,,,rrr,",true);
		assertMove("e1-3-d4",true,true);
		assertGameState("rr,rr,rr,rr,rr,rr/"
				+ 		",,,,,/"
				+ 		",,,rrr,,/"
				+ 		",,,,,/"
				+ 		",,,,,/"
				+ 		"b,,,,,",false,false,false,false);
	}

	@Test
	//schräg nach unten gegen die rechte Wand
	public void TestDownRightWall() {
		startGame("rr,rr,rr,rr,rr,rr/"
				+ ",,,,,/"
				+ ",,,,rrr,/"
				+ ",,,,,/"
				+ ",,,,,/"
				+ "b,,,,,",true);
		assertMove("e4-3-d1",true,true);
		assertGameState("rr,rr,rr,rr,rr,rr/"
				+ 		",,,,,/"
				+ 		",,,,,/"
				+ 		",,,,,/"
				+ 		",,,,,/"
				+ 		"b,,,rrr,,",false,false,false,false);
	}
		
	@Test
	public void TestTopWall() {
		startGame("rr,rr,rr,rr,rr,rr/rrr,,,,,/,,,,,/,,,,,/,,,,,/b,,,,,",true);
		assertMove("a5-3-a4",true,true);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/rrr,,,,,/,,,,,/,,,,,/b,,,,,",false,false,false,false);
	}

	@Test 
	//schräg nach rechts gegen die obere Wand
	public void TestRightTopWall() {
		startGame("rr,rr,rr,rr,rr,rr/"
				+ ",rrr,,,,/"
				+ ",,,,,/"
				+ ",,,,,/"
				+ ",,,,,/"
				+ "b,,,,,",true);
		assertMove("b5-3-e4",true,true);
		assertGameState("rr,rr,rr,rr,rr,rr/"
				+ 		",,,,,/"
				+ 		",,,,rrr,/"
				+ 		",,,,,/"
				+ 		",,,,,/"
				+ 		"b,,,,,",false,false,false,false);
	}

	@Test
	//schräg nach links gegen die obere Wand
	public void TestLeftTopWall() {
		startGame("rr,rr,rr,rr,rr,rr/"
				+ ",,,,rrr,/"
				+ ",,,,,/"
				+ ",,,,,/"
				+ ",,,,,/"
				+ "b,,,,,",true);
		assertMove("e5-3-b4",true,true);
		assertGameState("rr,rr,rr,rr,rr,rr/"
				+ 		",,,,,/"
				+ 		",rrr,,,,/"
				+ 		",,,,,/"
				+ 		",,,,,/"
				+ 		"b,,,,,",false,false,false,false);
	}
	
	@Test
	public void TestBotWall() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/rrr,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("a2-3-a3",true,true);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/rrr,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",false,false,false,false);
	}

	@Test 
	//schräg nach rechts gegen die untere Wand	
	public void TestRightBotWall() {
		startGame("rr,rr,rr,rr,rr,rr/"
				+ ",,,,,/"
				+ ",,,,,/"
				+ ",,,,,/"
				+ ",rrr,,,,/"
				+ "b,,,,,",true);
		assertMove("b2-3-e3",true,true);
		assertGameState("rr,rr,rr,rr,rr,rr/"
				+ 		",,,,,/"
				+ 		",,,,,/"
				+ 		",,,,rrr,/"
				+ 		",,,,,/"
				+ 		"b,,,,,",false,false,false,false);
	}

	@Test
	//schräg nach links gegen die untere Wand	
	public void TestLeftBotWall() {
		startGame("rr,rr,rr,rr,rr,rr/"
				+ ",,,,,/"
				+ ",,,,,/"
				+ ",,,,,/"
				+ ",,,,rrr,/"
				+ "b,,,,,",true);
		assertMove("e2-3-b3",true,true);
		assertGameState("rr,rr,rr,rr,rr,rr/"
				+ 		",,,,,/"
				+ 		",,,,,/"
				+ 		",rrr,,,,/"
				+ 		",,,,,/"
				+ 		"b,,,,,",false,false,false,false);
	}
	
	@Test
	//abprallen von der unteren linken Ecke	
	public void TestLeftBotCornerWall() {
		startGame("rr,rr,rr,rr,rr,rr/"
				+ ",,,,,/"
				+ ",,,,,/"
				+ ",,,,,/"
				+ ",rrr,,,,/"
				+ "b,,,,,",true);
		assertMove("b2-3-c3",true,true);
		assertGameState("rr,rr,rr,rr,rr,rr/"
				+ 		",,,,,/"
				+ 		",,,,,/"
				+ 		",,rrr,,,/"
				+ 		",,,,,/"
				+ 		"b,,,,,",false,false,false,false);
	}
	
	@Test
	//abprallen von der unteren rechten Ecke	
	public void TestRightBotCornerWall() {
		startGame("rr,rr,rr,rr,rr,rr/"
				+ ",,,,,/"
				+ ",,,,,/"
				+ ",,,,,/"
				+ ",,,,rrr,/"
				+ "b,,,,,",true);
		assertMove("e2-3-d3",true,true);
		assertGameState("rr,rr,rr,rr,rr,rr/"
				+ 		",,,,,/"
				+ 		",,,,,/"
				+ 		",,,rrr,,/"
				+ 		",,,,,/"
				+ 		"b,,,,,",false,false,false,false);
	}
	
	@Test
	//abprallen von der oberen rechten Ecke	
	public void TestRightTopCornerWall() {
		startGame("rr,rr,rr,rr,rr,rr/"
				+ ",rrr,,,,/"
				+ ",,,,,/"
				+ ",,,,,/"
				+ ",,,,,/"
				+ "b,,,,,",true);
		assertMove("b5-3-c4",true,true);
		assertGameState("rr,rr,rr,rr,rr,rr/"
				+ 		",,,,,/"
				+ 		",,rrr,,,/"
				+ 		",,,,,/"
				+ 		",,,,,/"
				+ 		"b,,,,,",false,false,false,false);
	}

	@Test
	//abprallen von der oberen linken Ecke	
	public void TestLeftTopCornerWall() {
		startGame("rr,rr,rr,rr,rr,rr/"
				+ ",,,,rrr,/"
				+ ",,,,,/"
				+ ",,,,,/"
				+ ",,,,,/"
				+ "b,,,,,",true);
		assertMove("e5-3-d4",true,true);
		assertGameState("rr,rr,rr,rr,rr,rr/"
				+ 		",,,,,/"
				+ 		",,,rrr,,/"
				+ 		",,,,,/"
				+ 		",,,,,/"
				+ 		"b,,,,,",false,false,false,false);
	}

	
	//failure tests

	@Test
	public void TestNoBlue() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/,,,,,",true);
		assertMove("a6-1-a5",true,true);
		assertGameState("r,rr,rr,rr,rr,rr/r,,,,,/,,,,,/,,,,,/,,,,,/,,,,,",false,true,false,true);
	}
	
	@Test
	public void TestEmptyField() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,,,,,",true);
		assertMove("b4-1-b3",true,false);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,,,,,",true, false, false, false);
	}
	
	@Test
	public void TestWrongField() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,,,,,", true);
		assertMove("a1-1-a2",true,false);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,,,,,", true, false, false, false);
	}
	
	
	@Test
	public void TestNotEnoughStones() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,,,,,", true);
		assertMove("a1-13-a3",true,false);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,,,,,", true, false, false, false);
	}

	@Test
	public void WrongTooTall() {
		startGame("rrrrrr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bbbbb,,,,,", false);
		assertMove("a1--a3",true,false);
	}
	
	@Test
	public void BlueRepeatingState() {
		
	}
	
	//TODO repeating state rule greift zu spät ein -> Wird der Startzustand nicht in die history gespeichert?
	@Test
	public void RedRepeatingState() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		//red
		assertMove("a6-1-a5", true, true);
		assertGameState("r,rr,rr,rr,rr,rr/r,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb", false, false, false, false);
		//blue
		assertMove("a1-1-a2", false, true);
		assertGameState("r,rr,rr,rr,rr,rr/r,,,,,/,,,,,/,,,,,/b,,,,,/b,bb,bb,bb,bb,bb", true, false, false, false);
		//red
		assertMove("a5-1-a6", true, true);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/b,,,,,/b,bb,bb,bb,bb,bb", false, false, false,false);
		//blue
		assertMove("a2-1-a1",false,true);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb", true, false, false, false);
		//red, third move so repeating state - draw should be true
		assertMove("a6-1-a5", true, true);
		assertGameState("r,rr,rr,rr,rr,rr/r,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb", false, false, false, false);
		//blue
		assertMove("a1-1-a2", false, true);
		assertGameState("r,rr,rr,rr,rr,rr/r,,,,,/,,,,,/,,,,,/b,,,,,/b,bb,bb,bb,bb,bb", true, false, false, false);
		//red
		assertMove("a5-1-a6", true, true);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/b,,,,,/b,bb,bb,bb,bb,bb", false, false, false,false);
		//blue
		assertMove("a2-1-a1",false,true);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb", true, false, false, false);
		//red
		assertMove("a6-1-a5", true, true);
		assertGameState("r,rr,rr,rr,rr,rr/r,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb", false, false, false, false);
		//blue
		assertMove("a1-1-a2", false, true);
		assertGameState("r,rr,rr,rr,rr,rr/r,,,,,/,,,,,/,,,,,/b,,,,,/b,bb,bb,bb,bb,bb", true, false, false, false);
		//red
		assertMove("a5-1-a6", true, true);
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/b,,,,,/b,bb,bb,bb,bb,bb", false, false, false,false);
		//blue
		assertMove("a2-1-a1",false,true);
		assertTrue(game.checkRepeatingState());
		assertGameState("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb", true, true, true, false);

	}
	
	
	//Die folgenden 6 Tests ändern nichts an der coverage innerhalb von DeathStacksGame, aber an der gesamten Coverage
	
	@Test
	public void TestMoveDirDownRight() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("a6-1-b5",true,true);
		assertGameState("r,rr,rr,rr,rr,rr/,r,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",false,false,false,false);
	}
	
	@Test
	public void TestMoveDirRight() {
		startGame("rr,rr,rr,rr,rr,rr/rr,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("a5-1-b5",true,true);
		assertGameState("rr,rr,rr,rr,rr,rr/r,r,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",false,false,false,false);
	}
	
	@Test
	public void TestmoveDirUpRight() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/rr,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("a4-1-b5",true,true);
		assertGameState("rr,rr,rr,rr,rr,rr/,r,,,,/r,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",false,false,false,false);
	}
	
	@Test
	public void TestmoveDirUp() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/rr,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("a4-1-a5",true,true);
		assertGameState("rr,rr,rr,rr,rr,rr/r,,,,,/r,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",false,false,false,false);
	}
	
	@Test
	public void TestmoveDirUpLeft() {
		startGame("rr,rr,rr,rr,rr,rr/,,,,,/,rr,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("b4-1-a5",true,true);
		assertGameState("rr,rr,rr,rr,rr,rr/r,,,,,/,r,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",false,false,false,false);
	}
	
	@Test
	public void TestmoveDirLeft() {
		startGame("rr,rr,rr,rr,rr,rr/,rr,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("b5-1-a5",true,true);
		assertGameState("rr,rr,rr,rr,rr,rr/r,r,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",false,false,false,false);
	}
	
	@Test
	public void TestMoveValidDistanceTooLong() {
		startGame("rr,rr,rr,rr,rr,rr/,,rr,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true);
		assertMove("c5-1-a5",true,false);
		assertGameState("rr,rr,rr,rr,rr,rr/,,rr,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb",true,false,false,false);
	}
	
	
	
}
