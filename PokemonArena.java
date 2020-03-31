//PokemonArena.java
//Nafiz Hasan
//A Pokemon arena game where the player chooses four Pokemon and then battles until they win or lose. They beat the Pokemon one at a time by going through a series of turn based battles where the player gets the option to attack, retreat, or pass. The turns are decided at random and the enemy Pokemon will be in a random order. The enemy can only attack or pass. If you beat every enemy Pokemon you win, and if you have no more remaining Pokemon to battle with you lose. All Pokemon are created from reading a text file containing the stats of the Pokemon and their attacks.

import java.util.*;
import java.io.*;

public class PokemonArena
{
	private static ArrayList<Pokemon>pokes = new ArrayList<Pokemon>(); //arraylist for all the Pokemon, after player chooses 4 Pokemon it will contain only enemy Pokemon
	private static ArrayList<Pokemon>playerPokes = new ArrayList<Pokemon>(); //all Pokemon the player can use
	private static ArrayList<Pokemon>deadPokes = new ArrayList<Pokemon>(); //Pokemon that have died on your team

	public static Pokemon BAD; //current Pokemon on the field
	public static Pokemon GOOD;

	public static int lastturnchecknum; //variable that decides when the round ends
	public static int turnchecknum; //variable that keeps track of the current turn

	public static final int MYTURN = 0; //constants for your turn and the opponents turn
	public static final int ENEMYTURN = 1;

	public static void main (String [] args) throws IOException
	{
		load(); //load the Pokemon
		System.out.println("Welcome to Pokemon Arena!");
		System.out.println("Please choose 4 Pokemon.");
	    choosePoke(); //choose your Pokemon
	    Collections.shuffle(pokes);

		while(playerPokes.size()!=0) //battle loop
		{
			battle();
			break;
		}

		if(playerPokes.size()==0) //if you lost the game
		{
			System.out.println("\nYou have no more remaining Pokemon.");
			System.out.println("You have lost.");
		}
		else //if you won the game
		{
			System.out.println("\nYou have beat all Pokemon!");
			System.out.println("You are TRAINER SUPREME!");
		}
	}

	public static void load()
	{
		try
		{
			Scanner inFile = new Scanner(new BufferedReader(new FileReader("pokemon.txt")));
			int n = Integer.parseInt(inFile.nextLine()); //uses the first line in the text file to get the total number of Pokemon
			for(int i=0; i<n; i++) //creates new Pokemon objects and adds them to the pokes list for every Pokemon
			{
				pokes.add(new Pokemon(inFile.nextLine()));
			}
			inFile.close();
		}
		catch(Exception e) //if there was no file
		{
			System.out.println("File was not found.");
			System.exit(1);
		}
	}

	public static void battle()
	{
		for(int i=0; i<pokes.size(); i++) //does the battle for every enemy Pokemon
		{
		    turnChecker(); //decides whose turn it is first

			System.out.println("\n- BATTLE "+(i+1)+" -"); //uses the for loop number to display what battle you are in
		    BAD = pokes.get(i); //bad pokemon is the current number in the for loop
		    GOOD = playerPokes.get(chooseStart()); //the good Pokemon is chosen by using the choostStart method for every time a battle starts

		    while(BAD.getHP()>0) //while the enemy is not defeated
	    	{
			    if(turnchecknum == MYTURN) //if it is your turn
			    {
			    	if(GOOD.getHP()<=0) //if your Pokemon is defeated
			    	{
						pokeDeath(); //removes the pokemon from the player Pokemon list
						if(playerPokes.size()!=0) //if you still have remaining Pokemon to use
						{
							System.out.println("Please choose another Pokemon.");
							GOOD = playerPokes.get(chooseStart()); //calls choose start again
						}
						else //if you have no more remaining Pokemon you have lost the game
						{
							break; //the loop breaks and the game ends
						}
			    	}
			    	else //if your Pokemons HP is more than 0 and is still alive
			    	{
				    	menu(GOOD, BAD); //calls the menu for the player
				    	turnchecknum = ENEMYTURN; //after action is chosen it is the enemies turn
			    	}
			    }
				if(turnchecknum == ENEMYTURN && BAD.getHP()>0) //if it is the enemies turn and it has not been defeated
				{
					printPokes(GOOD,BAD); //prints your Pokemon and the enemy Pokemon
					System.out.println("\nIt is "+(pokes.get(i)).getName()+"'s turn!");
					if(BAD.getStun()==false) //if the enemy is not stunned
					{
						badAttack(BAD,GOOD); //the enemy attacks
						endRound(); //the round ends if possible
						turnchecknum = MYTURN; //it is now the players turn
					}
					else //if the enemy is stunned it automatically passes and resets the stun for the next turn
					{
		    			System.out.println((pokes.get(i)).getName()+" is stunned!");
		    			System.out.println((pokes.get(i)).getName()+" has passed.");
		    			endRound(); //the round ends if possible
		    			BAD.resetStun();
		    			turnchecknum = MYTURN; //it is now the players turn
					}
				}
	    	}
	    	if(playerPokes.size()==0) //if the player has no Pokemon you lose
	    	{
	    		break; //breaks the loop and ends the game
	    	}
	    	else //if you do have Pokemon left
	    	{
				System.out.println("\n"+(pokes.get(i)).getName() +" has fainted!"); //displays the enemy fainting
				endBattle(); //ends the battle and goes to the next Pokemon
	    	}
		}
	}
	
	public static void turnChecker()
	{
	    Random rand = new Random(); //creates random numbers to decide who goes first
	    turnchecknum = rand.nextInt(2);

	    if(turnchecknum == MYTURN) //if it is your turn first, the round ends after the enemy goes
	    {
	    	lastturnchecknum = ENEMYTURN;
	    }
	    if(turnchecknum == ENEMYTURN) //if the enemy is first, the round ends after your turn
	    {
	    	lastturnchecknum = MYTURN;
	    }
	}

	public static void menu(Pokemon good, Pokemon bad)
	{
		Scanner kb = new Scanner(System.in);

		printPokes(good,bad);
		System.out.println("\nIt is your turn!");
		if(good.getStun() == false) //if the player is not stunned
		{
	    	int number;
			System.out.println("--------------------------------------------------------------------------------------------------------");
	    	System.out.println("1. Attack");
	    	System.out.println("2. Retreat");
	    	System.out.println("3. Pass");
			System.out.println("--------------------------------------------------------------------------------------------------------");
	    	System.out.println("Enter Action:");
	    	number = kb.nextInt();
	    	number = (checkValid(number, 3, 1)); //checks if number is valid
	    	if(number == 1) //goes to appropriate method based on what the player chooses
	    	{
	    		chooseAttack(good, bad);
	    	}
	    	if(number == 2)
	    	{
	    		chooseRetreat(good,bad);
	    	}
	    	if(number == 3)
	    	{
	    		choosePass();
	    	}
		}
		else //if the player is stunned it automatically passes for them and resets the stun for the next turn
		{
			System.out.println("\nYou are stunned!");
			System.out.println("You have passed.");
			endRound();
			GOOD.resetStun();
		}
	}

	public static int checkValid(int number, int highest, int lowest) //takes in the number entered, the highest possible number, and the lowest possible number
	{
		Scanner kb = new Scanner(System.in);
		int validnum;
		while(number>highest || number<lowest) //while the number is too high or low it keeps asking for a valid number
		{
			System.out.println("Please enter a valid number: ");
			number = kb.nextInt();
		}
		validnum = number;
		return number; //when a valid number is given it returns that number
	}

	public static void choosePoke() //choosing the 4 Pokemon at the beginning
	{
	    Scanner kb = new Scanner(System.in);
	    int number;
	    for(int p=0;p<4;p++) //4 times since you choose 4 Pokemon
	    {
	    	System.out.println("-------------------------------------------------------------------------------------------------------- ");
			for(int i=0; i<pokes.size(); i++)
			{
				System.out.println(String.format("%-4s%s" ,((i+1)+"."),(pokes.get(i))));
			}
			System.out.println("-------------------------------------------------------------------------------------------------------- ");
			System.out.println("Choose a Pokemon: ");
			number = kb.nextInt();
			number = (checkValid(number,pokes.size(),1)); //checks if number is valid
			playerPokes.add(pokes.get(number-1)); //adds that Pokemon to the player Pokemons list
			pokes.remove(pokes.get(number-1)); //removes it from the total Pokemon list to get only the enemies at the end
	    }
	}

	public static int chooseStart() //Pokemon to start the battle with
	{
		int number;
		Scanner kb = new Scanner(System.in);
		System.out.println("\nYour Pokemon:");
		System.out.println("--------------------------------------------------------------------------------------------------------");
	    for(int i=0; i<playerPokes.size(); i++) //displays current Pokemon you can choose
	    {
	    	System.out.println(String.format("%-4s%s" ,((i+1)+"."),(playerPokes.get(i))));
	    }
	    for(int i=0; i<deadPokes.size(); i++) //displays dead Pokemon that can not be chosen
	    {
	    	System.out.println("~.  "+deadPokes.get(i));
	    }
		System.out.println("--------------------------------------------------------------------------------------------------------");
	    System.out.println("Choose Starting Pokemon: ");
	    number = kb.nextInt();
	    number = (checkValid(number,playerPokes.size(),1)); //checks if number is valid
	    System.out.println("\n"+(playerPokes.get(number-1)).getName()+", I choose you!");
	    return(number-1); //returns the number so as to get the right Pokemon from the players list
	}

	public static void chooseAttack(Pokemon good, Pokemon bad) //allows the player to choose an attack
	{
		int back = 0; //variable to decide where the back button will be
		int number;
		boolean enough;
		Scanner kb = new Scanner(System.in);
		System.out.println("\nYour Attacks:");
		System.out.println("--------------------------------------------------------------------------------------------------------");
		for(int i=0; i<(good.getAttack()).size(); i++)
		{
			System.out.println((i+1)+". "+(good.getAttack()).get(i));
		}
		back = ((good.getAttack()).size()+1); //the back spot will always be the total number of attacks +1
		System.out.println(back+". Back");
		System.out.println("--------------------------------------------------------------------------------------------------------");
		System.out.println("Choose an Attack:");
		number = kb.nextInt();
		number = (checkValid(number,(good.getAttack()).size()+1,1)); //checks if number is valid
		if(number == back) //if the player chooses to go back it goes back to the menu
		{
			menu(good, bad);
		}
		else //if the player chooses to attack
		{
			enough = good.checkEnergy(number-1); //first checks if the player has enough energy to do the attack
			if(enough == false) //if not it goes back to the menu
			{
				System.out.println("\nYou do not have enough energy.");
				menu(good, bad);
			}
			if(enough == true) //if so it calls the attack method with the specified attack
			{
				good.attack(bad,number-1);
				endRound(); //after attacking the round ends if possible
			}
		}
	}

	public static void chooseRetreat(Pokemon good, Pokemon bad) //allows the player to switch to another Pokemon
	{
		int back = 0; //variable to decide where the back button will be
		int current = playerPokes.indexOf(good); //variable to check where the current Pokemon is in the list
		int number = 0;
		Scanner kb = new Scanner(System.in);
		System.out.println("\nYour Pokemon:");
		System.out.println("--------------------------------------------------------------------------------------------------------");
	    for(int i=0; i<playerPokes.size(); i++) //displays Pokemon that can be chosen
	    {
	      System.out.println((i+1)+". "+playerPokes.get(i));
	    }
	    for(int i=0; i<deadPokes.size(); i++) //displays dead Pokemon that can not be chosen
	    {
	      System.out.println("~. "+deadPokes.get(i));
	    }
	    back = playerPokes.size()+1; //back button spot is always the total Pokemon + 1
	    System.out.println(back+". Back");
		System.out.println("--------------------------------------------------------------------------------------------------------");
	    System.out.println("Choose who you want to switch to: ");
	    number = kb.nextInt();
	    number = (checkValid(number,playerPokes.size()+1,1)); //checks if the number is valid
	    if(number == back) //if player chooses to go back it goes back to the menu
		{
			menu(good, bad);
		}
	    else if(number == current+1) //if the player chooses a Pokemon that is already chosen it goes back to the menu
		{
			System.out.println("\nThis pokemon is already chosen.");
			menu(good, bad);
		}
		else //if the player chooses a valid Pokemon it makes that the new current Pokemon and ends the round if possible
		{
		    System.out.println("\n"+(playerPokes.get(number-1)).getName()+", I choose you!");
    		GOOD = playerPokes.get(number-1);
		    endRound();
		}
	}

	public static void choosePass() //allows the player to pass
	{
		System.out.println("\nYou passed.");
		endRound(); //ends round if possible
	}

	public static void badAttack(Pokemon bad, Pokemon good) //the attack choosing method for the enemy Pokemon
	{
		ArrayList<Attack>badAttacks = bad.getAttack(); //takes in all of the Pokemons attacks
	    Collections.shuffle(badAttacks); //shuffles them to make it a random order of attacks
	    for(int i=0; i<(bad.getAttack()).size(); i++)
	    {
	    	if(bad.checkEnergybad(badAttacks.get(i)) == true) //if the Pokemon has enough energy for an attack
	    	{
	    		bad.attackBad(good, badAttacks.get(i)); //goes through with the attack
	    		break; //breaks the loop because it made an action
	    	}
	    	else //if not the Pokemon passes
	    	{
	    		System.out.println(bad.getName()+" has passed.");
	    		break; //breaks the loop because it made an action
	    	}
	    }
	}

	public static void endRound() //ends the round if possible
	{
		if(turnchecknum == lastturnchecknum) //only ends the round if the seceond Pokemons turn has ended //both the player and the enemy have to go once for the round to end
		{
			System.out.println("\nThe round ended.");
			System.out.println("Your team recovered 10 energy.");
			for(int i=0; i<playerPokes.size(); i++)
			{
				(playerPokes.get(i)).recoverEnergy(); //recovers energy for all the players Pokemon
			}
			for(int i=0; i<pokes.size(); i++)
			{
				(pokes.get(i)).recoverEnergy(); //recovers energy for the enemy
			}
		}
	}

	public static void endBattle() //ends the battle
	{
		System.out.println("The battle ended.");
		System.out.println("Your team recovered 20 HP.");
		for(int i=0; i<playerPokes.size(); i++) //for all the players Pokemon
		{
			(playerPokes.get(i)).recoverHP(); //recovers 20 hp
			(playerPokes.get(i)).resetEnergy(); //resets energy for next battle
			(playerPokes.get(i)).resetStun(); //resets stun for next battle
			(playerPokes.get(i)).resetDisable(); //resets disable for next battle
		}
	}

	public static void pokeDeath() //removes players Pokemon because it died
	{
		System.out.println("\n"+GOOD.getName()+" has fainted!");
		deadPokes.add(playerPokes.get(playerPokes.indexOf(GOOD))); //adds to dead Pokemon list
		playerPokes.remove(playerPokes.get(playerPokes.indexOf(GOOD))); //removes from player list
	}

	public static void printPokes(Pokemon bad, Pokemon good) //prints the bad and good Pokemon currently on the field
	{
		//System.out.println("\n\n");
		System.out.println("\n========================================================================================================");
		System.out.println("========================================================================================================");
		System.out.println(bad);
		System.out.println(good);
		System.out.println("========================================================================================================");
	}
}

class Pokemon //creates Pokemon objects
{
	//creating variables for all the Pokemons stats
	
	//basic stats
	private String name;
	private int hp;
	private int totalhp;
	private String type;
	private String resistance;
	private String weakness;
	private int numAttacks;

	//attack stats
  	private String attackName;
	private int cost;
	private int damage;
	private String special;
  	ArrayList<Attack>attacks = new ArrayList<Attack>();

	//default energy
  	private int energy = 50;

	//special variables
  	private boolean stun = false;
  	private boolean disable = false;

	public Pokemon(String line) //creating the Pokemon objects
	{
		String [] stats = line.split(","); //splits the lines by the commas to get the seperate stats
		name = stats[0];
	    hp = Integer.parseInt(stats[1]);
	    totalhp = Integer.parseInt(stats[1]);
	    type = stats[2];
	    resistance = stats[3];
	    weakness = stats[4];
	    numAttacks = Integer.parseInt(stats[5]);
    	for (int i=0; i<numAttacks; i++) //creates attack objects for every attack a Pokemon has and then adds them to an array list for attacks
	    {
	    	//takes the position of the first attack stat and then adds 4*i to get the next attack because each attack has 4 stats, so the next one will be 4 over
	    	attackName = stats[6+4*i];
	    	cost = Integer.parseInt(stats[7+4*i]);
	    	damage = Integer.parseInt(stats[8+4*i]);
	    	special = stats[9+4*i];
	    	attacks.add(new Attack(attackName, cost, damage, special)); //creates attack object
	    }
	}

	public void resetEnergy() //resets energy back to the default which is 50
	{
		energy = 50;
	}

	public void recoverEnergy() //recovers energy by 10
	{
		if((energy + 10) <= 50) //if total is less than 50 when added, the energy increases by 10
		{
			energy += 10;
		}
		if((energy + 10) > 50) //if total is more than 50, the energy is 50 since it can not go over
		{
			energy = 50;
		}
	}

	public void recoverHP() //recovers HP by 20
	{
		if((hp + 20) <= totalhp) //if total is less than the max HP when added, the HP increases by 20
		{
			hp += 20;
		}
		if((hp + 20) > totalhp) //if total is more than the max HP, the HP is the max since it can not go over
		{
			hp = totalhp;
		}
	}

	public boolean checkEnergy(int at) //checks energy for attacks and returns true or false
	{
		Attack att = attacks.get(at);
		if((energy - att.getEnergy()) < 0) //if using the attack leaves you with less than 0 energy, return false
		{
			return false;
		}
		else //if not, return true
		{
			return true;
		}
	}

	public boolean checkEnergybad(Attack at) //does the same as checkEnergy but takes an attack instead of an int
	{
		return(checkEnergy(attacks.indexOf(at))); //finds the position of the attack in attacks list and calls checkEnergy
	}

	public void attack(Pokemon other, int at)
	{
		Attack att = attacks.get(at); //gets what attack is being used
		int disabledDamage = 0; //by default disabled damage is 0
		if(disable == true) //if Pokemon is disabled, disabled damage is 10
		{
			disabledDamage = 10;
		}
		System.out.println("\n"+name+" used "+att.getattackName()+".");
		double multiplier = 1; //multiplier for super effective and not very effective attacks
		double wildmultiplier = 1; //multiplier for wild card and wild storm attacks
		String spec = att.getSpecial(); //gets the special of that attack
		if(spec.equals("stun"))
		{
			System.out.println("It had a stun special!");
			boolean stunchecker = randomizer(); //gets true or false randomly
			if(stunchecker==true)
			{
				System.out.println(other.getName()+" got stunned!");
				other.stunSpecial(); //makes the Pokemon stunned
			}
			if(stunchecker==false)
			{
				System.out.println("But "+other.getName()+" did not get stunned!");
			}
		}
		if(spec.equals("wild card"))
		{
			System.out.println("It had a wild card special!");
			boolean misschecker = randomizer(); //gets true or false randomly
			if(misschecker == true)
			{
				System.out.println("It missed!");
				wildmultiplier = 0; //if it missed the wildmultiplier will be 0, doing 0 total damage
			}
			if(misschecker == false)
			{
				System.out.println("It hit!");
			}
		}
		if(spec.equals("wild storm"))
		{
			wildmultiplier = 0; //the defualt wild multiplier is 1 but if the special is wild storm the defualt will be 0
			int hitcounter = 0; //checks how many times it hit the other Pokemon
			boolean misschecker = randomizer(); //gets true of false randomly
			System.out.println("It had a wild storm special!");
			while(misschecker!=false) //does a while loop until the Pokemon misses
			{
				hitcounter+=1; //if misschecker is not false it means it hit
				misschecker = randomizer(); //gets true or false again
			}
			if(misschecker==false) //if misschecker is false the Pokemon missed and the wildmultiplier multiplies the damage by how many times it hit
			{
				wildmultiplier = hitcounter;
				System.out.println("It hit "+hitcounter+" times!");
			}
		}
		if(spec.equals("disable"))
		{
			System.out.println("It had a disable special!");
			System.out.println(other.getName()+" got disabled.");
			System.out.println(other.getName()+" will do 10 less damage for the rest of the battle.");
			other.disableSpecial(); //makes Pokemon disabled
		}
		if(spec.equals("recharge"))
		{
			System.out.println("It had a recharge special!");
			System.out.println(name+" recovered 20 energy");
			rechargeSpecial(); //recharges energy
		}
		if(wildmultiplier!=0) //if the wildmultiplier is 0 there is no point in super effective or not very effective as the damage wil be 0
		{
			if(type.equals(other.weakness)) //if the other Pokemon is weak to your attack the damage will be doubled
			{
				multiplier = 2;
				System.out.println("It was super effective!");
			}
			if(type.equals(other.resistance)) //if the other Pokemon is strong to your attack the damage will be cut in half
			{
				multiplier = 0.5;
				System.out.println("It was not very effective.");
			}
		}
		energy -= att.getEnergy(); //subtracts the energy cost of the attack from your energy
		double finalDamage = ((att.getDamage()*multiplier*wildmultiplier)-disabledDamage); //the final damage will be calculated by getting the base damage than multiplying by the normal multiplier and wild multiplier, and finally subtracting by the disabled damage
		if(finalDamage < 0)
		{
			finalDamage = 0; //if final damage is less than 0 it is made to 0 because you can not have negative damage
		}
		other.hp -= finalDamage; //subtracts the other Pokemons HP by the final damage
		if(other.hp<0)
		{
			other.hp = 0; //if the other Pokemons HP is less than 0 it is made to 0 //this is useful for showing the dead player Pokemon with 0 HP
		}
		System.out.println("It did "+finalDamage+" damage.");
	}

	public void attackBad(Pokemon other, Attack at) //does the same as attack but takes an attack instead of an int
	{
		attack(other, attacks.indexOf(at)); //finds the position of the attack in attacks list and calls attack
	}

	public boolean randomizer() //gives either true or false randomly
	{
		Random rand = new Random();
		int random = rand.nextInt(2);
		if(random == 0) //if 0 it is true
		{
			return(true);
		}
		if(random == 1) //if 1 it is false
		{
			return(false);
		}
		return(false);
	}

	public boolean getStun()
	{
		return stun; //returns the Pokemons stun variable
	}
	public void stunSpecial()
	{
		stun = true; //sets stun to true
	}
	public void resetStun()
	{
		stun = false; //resets stun to false
	}

	public void disableSpecial()
	{
		disable = true; //sets disable to true
	}
	public void resetDisable()
	{
		disable = false; //resets disable to false
	}

	public void rechargeSpecial() //adds 20 energy because the Pokemon had a recharge special
	{
		if((energy + 20) <= 50) //if total is less than 50 when added, the energy increases by 20
		{
			energy += 20;
		}
		if((energy + 20) > 50) //if total is more than 50, the energy is 50 since it can not go over
		{
			energy = 50;
		}
	}

	public int getHP()
	{
		return hp;
	}

	public ArrayList<Attack> getAttack()
	{
		return attacks;
	}

	public String getName()
	{
		return name;
	}

	public String toString()
	{
		return String.format("%-11s | HP: %3d/%-3d | ENERGY:%2d/50 | TYPE:%-8s | RESISTANCE:%-8s | WEAKNESS:%-8s |", name,hp,totalhp,energy,type,resistance,weakness); //used to display the Pokemons stats
	}
}

class Attack //creates attack objects
{
	//variables for the Pokemons attack stats
  	private String attackName;
	private int cost;
	private int damage;
	private String special;

	public Attack(String attackName, int cost, int damage, String special)
	{
	  this.attackName = attackName;
	  this.cost = cost;
	  this.damage = damage;
	  this.special = special;
	}

	public int getDamage()
	{
		return damage;
	}
	public int getEnergy()
	{
		return cost;
	}
	public String getattackName()
	{
		return attackName;
	}
	public String getSpecial()
	{
		return special;
	}
	public String toString()
	{
		return String.format("%-13s ENERGY:%-3d DAMAGE:%-3d SPECIAL: %s", attackName,cost,damage,special); //used to display the attack stats
	}
}