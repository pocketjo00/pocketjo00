package application;

/*******
 * <p> Title: PasswordEvaluationTestingAutomation Class. </p>
 * 
 * <p> Description: A Java demonstration for semi-automated tests </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2022 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00	2022-02-25 A set of semi-automated test cases
 * @version 2.00	2024-09-22 Updated for use at ASU
 * 
 */
public class UsernameEvaluationTestingAutomation {
	
	static int numPassed = 0;	// Counter of the number of passed tests
	static int numFailed = 0;	// Counter of the number of failed tests

	/*
	 * This mainline displays a header to the console, performs a sequence of
	 * test cases, and then displays a footer with a summary of the results
	 */
	public static void main(String[] args) {
		System.out.println("______________________________________");
		System.out.println("\nTesting Automation");

		performTestCase(1, "John_Doe123", true);
		performTestCase(2, "_InvalidStart", false);
		performTestCase(3, "Short", true);
		performTestCase(4, "TooLongUserName_12345", false);
		performTestCase(5, "Valid-Name.42", true);

		System.out.println("____________________________________________________________________________");
		System.out.println();
		System.out.println("Number of tests passed: " + numPassed);
		System.out.println("Number of tests failed: " + numFailed);
	}

	private static void performTestCase(int testCase, String inputText, boolean expectedPass) {
		System.out.println("____________________________________________________________________________\n\nTest case: " + testCase);
		System.out.println("Input: \"" + inputText + "\"");
		System.out.println("______________");
		System.out.println("\nFinite state machine execution trace:");

		String resultText = UserNameRecognizer.checkForValidUserName(inputText);

		System.out.println();

		if (!resultText.isEmpty()) {
			if (expectedPass) {
				System.out.println("***Failure*** The username <" + inputText + "> is invalid. But it was supposed to be valid!");
				System.out.println("Error message: " + resultText);
				numFailed++;
			} else {
				System.out.println("***Success*** The username <" + inputText + "> is invalid, as expected.");
				numPassed++;
			}
		} else {
			if (expectedPass) {
				System.out.println("***Success*** The username <" + inputText + "> is valid, as expected.");
				numPassed++;
			} else {
				System.out.println("***Failure*** The username <" + inputText + "> was judged as valid, but it was supposed to be invalid!");
				numFailed++;
			}
		}
	}
}