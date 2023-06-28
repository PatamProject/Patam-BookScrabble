# Patam-BookScrabble

Please do not touch the master branch!

![image](https://github.com/PatamProject/Patam-BookScrabble/assets/106737885/b0bd65fe-8ea8-45a3-bcee-91ebd95b6a2c)


Project Overview:
The goal of this project is to develop a Book Scrabble game with a client-server architecture and Socket communications. The game allows players to search for words in a dictionary and challenge the existence of words in books. It incorporates caching mechanisms to optimize word queries and utilizes a Bloom filter for efficient word existence checks. The project includes both server-side and client-side implementations.

Server-side Implementation:
The server-side implementation consists of several components:

Cache Handler: This package contains classes related to caching mechanisms. It includes the CacheManager class, which manages a cache with a specified size and replacement policy. The CacheReplacementPolicy interface defines the behavior of cache replacement policies such as Least Recently Used (LRU) and Least Frequently Used (LFU). The LRU and LFU classes provide implementations of these policies.

Dictionary: This package handles dictionary operations. The Dictionary class initializes the dictionary using input files and provides methods for word queries and challenges. It also utilizes caching mechanisms to optimize word lookups.

Dictionary Manager: The DictionaryManager class acts as a central manager for multiple dictionaries. It maintains a map of dictionaries and provides methods for querying and challenging words across all dictionaries.

IO Searcher: This package handles file I/O operations. The IOSearcher class includes methods for searching words in files and extracting words from files.

Server Handler: This package contains classes for handling client connections and requests. The BookScrabbleHandler class implements the ClientHandler interface and defines the logic for handling client requests. The MyServer class is responsible for starting and managing the server socket, accepting client connections, and delegating client requests to the appropriate handler.

Client-side Implementation:
The client-side implementation includes a RunClient class that represents the client application. It connects to the host server and interacts with it by sending queries and challenges. The client application can be run as a host or a guest, allowing players to join games hosted by others or host their own games. Each host functions as a server for their guests and as a client to the BookScrabbleServer. The connection to each guest remains persistent during the game.

In this project, the client-side implementation can be divided into three main components:

Model: The Model component of the host consists of classes such as Word, Tile, Board, Rack, PlayerModel, GameManager, and GameModel. These classes represent the data and business logic of the Book Scrabble game. They encapsulate the game rules, scoring mechanisms, game state, and player information. The Model component is responsible for managing the data and implementing the game-specific functionality. The host holds the information for their game and sends it to all of their guests. The host uses the classes ClientModel, ClientSideHandler, HostSideHandler, and MyHostHandler to handle the communication between their Model and the Models of their guests and between their Model and the BookScrabbleServer. This communication allows for updating the game based on user input and propagating changes to the View. Each guest has a simple implementation of the Model component that receives updates from the host.

<<<<<<< Updated upstream
View: The View component represents the user interface of the Book Scrabble game. It includes graphical elements such as buttons, text fields, and game board representation. The View is designed to be responsive, adapting to different screen sizes and orientations to provide an optimal user experience across devices. By utilizing responsive design principles, the View ensures that the game interface adjusts dynamically to fit the available screen space. This responsiveness allows players to enjoy the game on various devices, including desktop computers, laptops, tablets, and smartphones. The responsive design of the View component enables elements to resize and reposition themselves automatically based on the screen size. This ensures that all game-related information and controls remain accessible and visually appealing, regardless of the device used to play the game.

ViewModel: The ViewModel component acts as an intermediary between the Model and the View. It provides a layer of abstraction that exposes the necessary data and functionality from the Model to the View. The ViewModel translates the user's interactions with the View into actions that affect the underlying Model. It also notifies the View of any changes in the Model, allowing the View to update its display accordingly. Although not explicitly mentioned in the current state of the project, the ViewModel will be added in the near future. Its inclusion will enhance the modularity and extensibility of the application, making it easier to manage and update the user interface independently from the game logic.
=======
2. View: The View component represents the user interface of the Book Scrabble game. It consists of several FXML pages and corresponding Java classes that handle the functionality of each page. The View is designed to be responsive, providing an optimal user experience across different devices and screen sizes. Additionally, each page and button is connected to the Model through the ViewModel, promoting a separation of concerns and ensuring efficient data flow. Here are the details of the FXML pages, their associated Java classes, and their responsiveness:

    a.GameWindow.fxml: This FXML page represents the main game window, where players can interact with the game board, tiles, and other game elements. It displays the game state and allows players to make moves and perform actions. The corresponding Java class is GameWindowController.java. The page layout and game board are responsive, adapting to different screen sizes and orientations. The ViewModel associated with this page serves as an intermediary between the Model and the View, updating the game state and facilitating user interactions.

    b.GuestGameLobby.fxml: This FXML page is the game lobby for guests. It displays the game settings and allows guests to join a host's game. The corresponding Java class is GuestGameLobbyController.java. The page layout and input fields are responsive, ensuring that guests can easily enter the necessary information. The ViewModel associated with this page communicates with the Model to retrieve game settings and handles the guest's actions.

    c.HostGameLobby.fxml: This FXML page is the game lobby for hosts. It allows hosts to configure the game settings and invite guests to join their game. The corresponding Java class is HostGameLobbyController.java. The page layout and input fields are responsive, providing a smooth experience for hosts. The ViewModel associated with this page interacts with the Model, updating the game settings and managing guest invitations.

    d.HostMenu.fxml: This FXML page represents the host menu, where hosts can manage their games and access various game-related functionalities. The corresponding Java class is HostMenuController.java. The page layout and buttons are responsive, adapting to different screen sizes. The ViewModel associated with this page connects to the Model, enabling hosts to perform game management tasks and access relevant data.

    e.Main.fxml: This FXML page is the main application window that serves as the entry point for the application. It provides navigation and access to different screens and functionalities. The corresponding Java class is MainController.java. The page layout and navigation buttons are responsive, allowing users to easily navigate between different screens. The ViewModel associated with this page coordinates the navigation and data flow between the Model and the View.

Each Java class handles the functionality and responsiveness of its associated FXML page. The ViewModel associated with each page acts as a bridge between the View and the Model, ensuring a smooth and efficient data flow between the components. This separation of concerns and use of the ViewModel pattern enhances the maintainability, testability, and flexibility of the application.

3.ViewModel:
The `ViewModel` component serves as a crucial intermediary between the Model and the View. It plays a vital role in ensuring a separation of concerns and facilitating efficient data flow between these components. In this project, although the ViewModel is not explicitly mentioned, it is essential to consider its future implementation for improved maintainability and extensibility.

The ViewModel acts as a bridge between the Model and the View, providing a layer of abstraction that exposes the necessary data and functionality from the Model to the View. It encapsulates the business logic and state of the application, enabling the View to interact with the underlying Model without directly accessing it. By doing so, the ViewModel promotes modularity and enhances the testability of the application.

The primary responsibilities of the ViewModel are as follows:

1. Data Exposition: The ViewModel exposes relevant data from the Model to the View, allowing the View to display the necessary information to the user. This includes game state, player information, scores, and other relevant data. By providing a well-defined interface for accessing data, the ViewModel ensures that the View can retrieve the required information easily.

2. User Interaction Translation: The ViewModel translates the user's interactions with the View into actions that affect the underlying Model. It receives user input from the View and processes it accordingly, triggering the appropriate actions in the Model. For example, when a player makes a move on the game board, the ViewModel receives this interaction, validates it, and updates the Model accordingly.

3. Model-View Synchronization: The ViewModel serves as a communication hub, notifying the View of any changes in the Model. When the Model undergoes updates, such as changes in game state or player scores, the ViewModel relays these changes to the View. This enables the View to update its display promptly, ensuring that the user sees the most up-to-date information.

By incorporating the ViewModel into the application architecture, you can achieve the following benefits:

- Separation of Concerns: The ViewModel separates the concerns of data management and user interface from the Model, ensuring a cleaner and more maintainable codebase.

- Improved Testability: With the ViewModel acting as an intermediary, it becomes easier to write unit tests for the View and the Model independently. The ViewModel can be tested in isolation, verifying its functionality and data manipulation.

- Flexibility and Extensibility: The ViewModel provides a flexible layer that can adapt to future changes and additions in the application's functionality. It allows for easy integration of new features without significantly impacting the existing codebase.

Therefore, in the context of this project, implementing the ViewModel component would enhance the architecture by facilitating better communication, promoting separation of concerns, and enabling future extensibility.
>>>>>>> Stashed changes

By adopting the MVVM architecture, the project achieves separation of concerns and improves the maintainability and testability of the client-side implementation. The Model encapsulates the game logic and data, making it reusable and easily testable. The ViewModel serves as a bridge between the Model and the View, allowing for decoupling and easier management of the user interface. The use of MVVM promotes modular design, code reusability, and flexibility in extending or modifying the application's functionality.

Testing:
The project includes a Test package for testing the different classes of the project in the style of Unit Testing.

Getting Started:
To run the project, follow these steps:

Compile the source code.
Start the server by running the MyServer class with the desired port number.
Run the client application using the TestRunClient class.
Choose the appropriate options (host/guest, server IP, and port) when prompted.
Play the Book Scrabble game by adding words to the board, searching for words in the dictionary, and challenging their existence in the dictionary.
Note: The project assumes that the necessary input files, such as the dictionary and book files, are available in the specified locations.

Dependencies:
The project relies on the following dependencies:
Java SE Development Kit (JDK)
Java Standard Library


Contributors:
Itay Evron
Ofek Shpirer
Maya Geva
Uri Beeri
