# Chat Application README

This README provides an overview of the Chat application, its activity flow, and instructions for setting up and running the application.

## Execution Instructions

To set up and run the Chat application, follow these steps:

### Prerequisites

*   Android Studio installed.
*   An Android device or emulator for testing.
*   Java Development Kit (JDK) installed.

### Setup

1.  **Clone the Repository**:
    ```bash
    git clone https://github.com/Sri-Aryan/Chat.git
    ```
2.  **Open in Android Studio**:
    *   Launch Android Studio.
    *   Select `Open an existing Android Studio project`.
    *   Navigate to the cloned `Chat` directory and open it.

3.  **Sync Gradle Project**:
    *   Android Studio will automatically try to sync the Gradle project. If it doesn't, click on `Sync Project with Gradle Files` button in the toolbar.

4.  **Configure CometChat App Credentials**:
    *   The application uses CometChat for its chat functionalities. You will need to obtain your own App ID and Auth Key from the CometChat Dashboard.
    *   In Android Studio, navigate to `app/src/main/java/com/example/chat/AppCredentials.kt`.
    *   Replace the placeholder values for `APP_ID` and `AUTH_KEY` with your actual CometChat credentials:
        ```kotlin
        object AppCredentials {
            const val APP_ID = "YOUR_APP_ID" // Replace with your CometChat App ID
            const val AUTH_KEY = "YOUR_AUTH_KEY" // Replace with your CometChat Auth Key
        }
        ```

### Running the Application

1.  **Select a Device/Emulator**:
    *   In Android Studio, select your target Android device or emulator from the dropdown menu in the toolbar.

2.  **Run the Application**:
    *   Click the `Run 'app'` button (green play icon) in the toolbar.
    *   Android Studio will build and install the application on your selected device/emulator.

Once the application launches, you will see the splash screen, and then be guided through the login or credential setup process as described in the "Flow of Activity" section.



## Libraries Used

The Chat application utilizes the following key libraries:

*   **AndroidX Core KTX**: Essential AndroidX extensions for Kotlin.
*   **AndroidX AppCompat**: Provides backward compatibility for new Android features.
*   **Material Design**: Implements Material Design components and themes.
*   **AndroidX Activity**: Provides components for managing activity lifecycle.
*   **AndroidX ConstraintLayout**: Allows you to create flexible and responsive layouts.
*   **JUnit**: A popular unit testing framework for Java.
*   **AndroidX JUnit**: JUnit extensions for Android testing.
*   **AndroidX Espresso Core**: A testing framework for UI tests on Android.
*   **CometChat UI Kit**: Provides pre-built UI components for CometChat functionalities.
*   **CometChat Calls SDK (Optional)**: Used for voice and video calling features.

