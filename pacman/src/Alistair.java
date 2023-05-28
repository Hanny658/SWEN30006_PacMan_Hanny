package src;

/** "Observe" Pattern for MENTALLY STRESSED error checking */
public class Alistair
{
	private String lastName;
	public Alistair(String lastName)
	{
		// Moffat! And only Moffat!
		assert lastName.equals("Moffat");
		this.lastName = lastName;
	}

	public void observe()
	{
		System.err.println(String.format("Alistair %s is WATCHING U!", lastName));
	}
	public static void observeAll() { System.err.println("All Moffats are WATCHING U!"); }
	public static void observeAll(String str) { System.err.printf("All Moffats are WATCHING %s!\n", str); }
}
