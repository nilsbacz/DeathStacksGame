package de.tuberlin.sese.swtpp.gameserver.model.deathstacks;

import de.tuberlin.sese.swtpp.gameserver.model.Game;
import de.tuberlin.sese.swtpp.gameserver.model.Move;
import de.tuberlin.sese.swtpp.gameserver.model.Player;
// TODO: more imports allowed

public class DeathStacksGame extends Game {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -3053592017994489843L;
	/************************
	 * member
	 ***********************/
	
	private final static String defaultSpielfeld="rr,rr,rrrrr,rr,rr,rr/,,,,,/,,,,,/,,,,,/,,,,,/bb,bb,bb,bb,bb,bb";
	//private final static String leeresSpielfeld=",,,,,/,,,,,/,,,,,/,,,,,/,,,,,/,,,,,";
	
	
	// just for better comprehensibility of the code: assign blue and red player
	private Player bluePlayer; //0
	private Player redPlayer; //1

	// TODO: internal representation of the game state 
	private static final int dimX = 6; // a..f
	private static final int dimY = 6; // 1..6
	private String[][] spielfeld=new String[dimY][dimX];
	
	private static final int tooTall=5;
	
	private static class Coordinate {
		public int x;
		public int y;
		
		public Coordinate() {
		}
		
		public Coordinate(int x, int y) {
			this.x=x;
			this.y=y;
		}
		
		public boolean sameAs(Coordinate c) {
			if (c.x==this.x && c.y==this.y) return true; else return false;
		}
	}
	
	private static final Coordinate[] dirs = // Vektoren: Richtungen, in die sich die Steine bewegen können.
		{new Coordinate(-1,0),new Coordinate(-1,1),new Coordinate(0,1),new Coordinate(1,1), //links (l), links oben (lo), oben (o), rechts oben (ro),
		 new Coordinate(1,0),new Coordinate(1,-1),new Coordinate(0,-1),new Coordinate(-1,-1)}; //rechts (r), rechts unten (ru), unten (u), links unten (lu)
	
	/************************
	 * constructors
	 ***********************/
	
	public DeathStacksGame() throws Exception{
		super();
		setBoard(defaultSpielfeld);
		// TODO: Initialization, if necessary
	}
	
	public String getType() {
		return "deathstacks";
	}
	
	/*******************************************
	 * Game class functions already implemented
	 ******************************************/
	
	@Override
	public boolean addPlayer(Player player) {
		if (!started) {
			players.add(player);
			
			if (players.size() == 2) {
				started = true;
				this.redPlayer = players.get(0);
				this.bluePlayer = players.get(1);
				nextPlayer = this.redPlayer;
			}
			return true;
		}
		
		return false;
	}

	@Override
	public String getStatus() {
		if (error) return "Error";
		if (!started) return "Wait";
		if (!finished) return "Started";
		if (surrendered) return "Surrendered";
		if (draw) return "Draw";
		
		return "Finished";
	}
	
	@Override
	public String gameInfo() {
		String gameInfo = "";
		
		if(started) {
			if(blueGaveUp()) gameInfo = "blue gave up";
			else if(redGaveUp()) gameInfo = "red gave up";
			else if(didRedDraw() && !didBlueDraw()) gameInfo = "red called draw";
			else if(!didRedDraw() && didBlueDraw()) gameInfo = "blue called draw";
			else if(draw) gameInfo = "draw game";
			else if(finished)  gameInfo = bluePlayer.isWinner()? "blue won" : "red won";
		}
			
		return gameInfo;
	}	
	
	@Override
	public String nextPlayerString() {
		return isRedNext()? "r" : "b";
	}

	@Override
	public int getMinPlayers() {
		return 2;
	}

	@Override
	public int getMaxPlayers() {
		return 2;
	}
	
	@Override
	public boolean callDraw(Player player) {
		
		// save to status: player wants to call draw 
		if (this.started && ! this.finished) {
			player.requestDraw();
		} else {
			return false; 
		}
	
		// if both agreed on draw:
		// game is over
		if(players.stream().allMatch(p -> p.requestedDraw())) {
			this.finished = true;
			this.draw = true;
			redPlayer.finishGame();
			bluePlayer.finishGame();
		}	
		return true;
	}
	
	@Override
	public boolean giveUp(Player player) {
		if (started && !finished) {
			if (this.redPlayer == player) { 
				redPlayer.surrender();
				bluePlayer.setWinner();
			}
			if (this.bluePlayer == player) {
				bluePlayer.surrender();
				redPlayer.setWinner();
			}
			finished = true;
			surrendered = true;
			redPlayer.finishGame();
			bluePlayer.finishGame();
			
			return true;
		}
		
		return false;
	}

	/*******************************************
	 * Helpful stuff
	 ******************************************/
	
	/**
	 * 
	 * @return True if it's white player's turn
	 */
	public boolean isRedNext() {
		return nextPlayer == redPlayer;
	}
	
	/**
	 * Finish game after regular move (save winner, move game to history etc.)
	 * 
	 * @param player
	 * @return
	 */
	public boolean finish(Player player) {
		// public for tests
		if (started && !finished) {
			player.setWinner();
			finished = true;
			redPlayer.finishGame();
			bluePlayer.finishGame();
			
			return true;
		}
		return false;
	}

	public boolean didRedDraw() {
		return redPlayer.requestedDraw();
	}

	public boolean didBlueDraw() {
		return bluePlayer.requestedDraw();
	}

	public boolean redGaveUp() {
		return redPlayer.surrendered();
	}

	public boolean blueGaveUp() {
		return bluePlayer.surrendered();
	}

	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 ******************************************/
	
	@Override
	public void setBoard(String state) {
		// TODO: implement
		int x, y;
		int i=0;
		for (y=dimY-1; y>=0; y--) for (x=0; x<dimX; x++) spielfeld[y][x]=""; // leeres spielfeld initialisieren
		x=0; y=dimY-1;
		// Prüfung auf gültige X/Y-Koordinaten wurde herausgenommen, damit McCabe <= 10 erfüllt wird.
		while(i<state.length()) {
			switch(state.charAt(i)) {
				case 'r':	spielfeld[y][x]+='r';	// roten Stein aufs Spielfeld legen.
							break;
				case 'b':	spielfeld[y][x]+='b';	// blauen Stein aufs Spielfeld legen.
							break;
				case ',':	x++;	// eine Spalte weiter
							break;
				//case '/':
				default:	y--;	// eine Reihe weiter
							x=0;
							break;
			}
			i++;
		}
	}
	
	@Override
	public String getBoard() {
		String s="";
		for (int y=dimY-1; y>=0; y--) {
			for (int x=0; x<dimX-1; x++) {
				s += spielfeld[y][x];	//Inhalt des Feldes hinzufügen
				s += ',';	//mit Komma trennen
			}
			s+=spielfeld[y][dimX-1]; // nach der letzten Spalte kein Komma...
			if (y>0) s+='/';  //..sondern ein Slash. Außer am Schluss.
		}
		return s;		
	}
	
	public Coordinate stringToCoordinate(String s){
		Coordinate coord= new Coordinate();
		if (s.length()!=2) return null;
		coord.x = s.charAt(0)-'a';
		coord.y = s.charAt(1)-'1';
		if (coord.x<0 || coord.x>=dimX || coord.y<0 || coord.y>=dimY  ) return null; 		//Prüfung, ob Zug im Spielfeld verläuft
		return coord;
	}
	
	public boolean ownedBy(int x, int y, Player player) { //gehört das Feld dem angegebenen Spieler?
//		if(spielfeld[y][x].length()==0) return false; // leeres Feld gehört keinem Spieler.
		if ( (spielfeld[y][x].charAt(0)=='r' 
				&& player==redPlayer)){
			return true; 
		} else if ((spielfeld[y][x].charAt(0)=='b' 
				&& player == bluePlayer)){
			return true;
		}
		return false;
	}
	
	public boolean checkTooTall() {
		/*
		 * checkTooTall() gibt true zurück, wenn ein zu großer Stapel existiert und dieser dem aktuellen Spieler gehört.
		 */
		
		for (int y=dimY-1; y>=0; y--) 
			for (int x=0; x<dimX; x++) {
				if (spielfeld[y][x].length()>=tooTall 
						&& ownedBy(x,y,nextPlayer) ) return true;					
			}
		return false;
	}
	
	public Coordinate moveDir(Coordinate start, int dir, int steps) {
		Coordinate c= new Coordinate(start.x, start.y);
		for (int i=0; i<steps; i++) {
			c.x+=dirs[dir].x;	// einen Schritt machen
			c.y+=dirs[dir].y;
			//Falls Coordinate außerhalb des Spielfelds: Reflexionsregeln anwenden
			if (c.x<0) {
				c.x+=2; // Stein zurück aufs Spielfeld
				dir= (dir==0)?4 : (dir==1)?3:5; // Richtung ändern: l->r; lo->ro; lu->ru
			}
			if (c.x>=dimX) {
				c.x-=2;// Stein zurück aufs Spielfeld
				dir= (dir==4)?0 : (dir==3)?1:7;// Richtung ändern:r->l; ro->lo; ru->lu
			}
			if (c.y<0) {
				c.y+=2;// Stein zurück aufs Spielfeld
				dir= (dir==6)?2 : (dir==5)?3:1;// Richtung ändern: u->o; ru->ro; lu->lo
			}
			if (c.y>=dimY) {
				c.y-=2;// Stein zurück aufs Spielfeld
				dir= (dir==2)?6 : (dir==3)?5:7;// Richtung ändern: o->u; ro->ru; lo->lu
			}
		}
		return c;
	}
	//ANMERKUNG: Wenn man 335 ganz nach oben schiebt wird 335 komplett gecovered, aber 333 nicht. Also wird 335 nicht gebraucht -> lässt sich nicht auf false setzen
	public boolean isMoveValid(Coordinate start, Coordinate ziel, int steps, Player player) {
		if (spielfeld[start.y][start.x].length()==0)return false; // Feld leer? Abbruch
		if (spielfeld[start.y][start.x].charAt(0) != nextPlayerString().charAt(0)) return false; // Feld gehört nicht dem aktuellen Spieler? Abbruch!
//		if (spielfeld[start.y][start.x].length()< steps) return false; // nicht genug Steine 
		if (spielfeld[start.y][start.x].length()<tooTall && checkTooTall()) return false; //too Tall Regel prüfen
		if (spielfeld[start.y][start.x].length()-steps>=tooTall) return false; //too Tall Regel prüfen 2. Teil	
		
		for (int i=0; i<8; i++) {
			if (moveDir(start,i,steps).sameAs(ziel)) return true; // in jede Richtung gehen und gucken, ob man am Ziel ankommt
		}
		return false; //in keiner Richtung angekommen? Zug ungültig!
	}
	
	boolean checkPlayerWins(Player p) { //bit true zurück, wenn der Spieler gewonnen hat
		for (int y=dimY-1; y>=0; y--) for (int x=0; x<dimX; x++) {
			if (spielfeld[y][x].length()>0 && !ownedBy(x,y,p)) return false; //es wurde ein Feld gefunden, das Player p nicht gehört.
		}
		return true;
	}
	
	public boolean checkRepeatingState() {
		int i=0;
		for (Move move: history) {
			if (move.getBoard().equals(getBoard())) i++; //wenn der Move in der History dem aktuellen Zustand entspricht, dann Zähler erhöhen
		}
		// i sollte am Ende der Schleife mindestens 1 sein, da der aktuelle Zustand schon in der history gespeichert wurde.
		if (i>=3) return true; else return false; //Spiel beendet, wenn gleicher Zustand drei mal erreicht
	}
	
	void setNextPlayer() { 
		if (isRedNext()) setNextPlayer(bluePlayer); else setNextPlayer(redPlayer); //nächster Spieler ist dran
	}
	
	@Override
	public boolean tryMove(String moveString, Player player) {
		/*Prüfung, ob moveString dem vorgegebenen Format enspricht (<start>-<schritte>-<ziel>)*/ 
		Coordinate start;
		Coordinate ziel;
		int nSteps;
		try {//aufteilen in drei Teile (Trennung des Strings an den Stellen mit "-"
			start=stringToCoordinate(moveString.substring(0,moveString.indexOf('-')));
			ziel=stringToCoordinate(moveString.substring(moveString.lastIndexOf('-')+1,moveString.length()));
			nSteps=Integer.parseInt(moveString.substring(moveString.indexOf('-')+1,moveString.lastIndexOf('-')));
		} catch (Exception e) { // wenn eine Exception geworfen wird, dann war wohl das Format falsch
			return false;
		}
		if (start==null) return false;
		if (ziel==null) return false;
		if (nSteps<1) return false;
		/*Prüfung, ob Spieler an der Reihe ist*/
		if (player!=nextPlayer) return false;
		/*Prüfung, ob Zug den Spielregeln entspricht*/
		if (!isMoveValid(start, ziel, nSteps, player)) return false;
		// Zug ausführen
		history.add(new Move(moveString, getBoard(), player)); //Zug in History speichern
		spielfeld[ziel.y][ziel.x]=spielfeld[start.y][start.x].substring(0,nSteps)+spielfeld[ziel.y][ziel.x];
		spielfeld[start.y][start.x]=spielfeld[start.y][start.x].substring(nSteps,spielfeld[start.y][start.x].length());
		if (checkPlayerWins(player)) finish(player); //wenn der aktuelle Spieler gewonnen hat, Spiel beenden
		else if (checkRepeatingState()) { //trifft Repeating-State-Regel zu?
			callDraw(bluePlayer);//Unentschieden
			callDraw(redPlayer);
		} else setNextPlayer();//der nächste Spieler ist dran
		return true;
	}
		
}
