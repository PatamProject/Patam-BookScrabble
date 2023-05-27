# Patam-BookScrabble

Please do not touch the master branch!

![image](https://github.com/PatamProject/Patam-BookScrabble/assets/106737885/b0bd65fe-8ea8-45a3-bcee-91ebd95b6a2c)


Project Overview:
The project aims to develop a Book Scrabble game with a client-server architecture and Sockets communications. The game allows players to search for words in a dictionary and challenge the existence of words in books. It incorporates caching mechanisms to optimize word queries and utilizes a Bloom filter for efficient word existence checks. The project includes both server-side and client-side implementations. 


Server-side Implementation:
Each host can connect to the BookScrabbleServer that runs seperetly and ask for existence of words in the dictionary's.
In this line of communication a host will connect to the server for a single question and will disconnect right after the acceptance of an answer.

The server-side implementation consists of several components:

Cache Handler: This package contains classes related to caching mechanisms. It includes the CacheManager class, which manages a cache with a specified size and replacement policy. The CacheReplacementPolicy interface defines the behavior of cache replacement policies such as Least Recently Used (LRU) and Least Frequently Used (LFU). The LRU and LFU classes provide implementations of these policies.

Dictionary: This package handles the dictionary operations. The Dictionary class initializes the dictionary using input files and provides methods for word queries and challenges. It also utilizes caching mechanisms to optimize word lookups.

Dictionary Manager: The DictionaryManager class acts as a central manager for multiple dictionaries. It maintains a map of dictionaries and provides methods for querying and challenging words across all dictionaries.

IO Searcher: This package handles file I/O operations. The IOSearcher class includes methods for searching words in files and extracting words from files.

Server Handler: This package contains classes for handling client connections and requests. The BookScrabbleHandler class implements the ClientHandler interface and defines the logic for handling client requests. The MyServer class is responsible for starting and managing the server socket, accepting client connections, and delegating client requests to the appropriate handler.


Client-side Implementation:
The client-side implementation includes a RunClient class that represents the client application. It connects to the host server and interacts with it by sending queries and challenges. The client application can be run as a host or a guest, allowing players to join games hosted by others or host their own games.
Each host will function as a server for his guests and as a client to the BookScrabbleServer. The connection to each guest will be persistent during the game. 

In this project, the client-side implementation can be divided into three main components:

1. Model: The Model component of the host consists of classes such as Word, Tile, Board, Rack, PlayerModel, GameManager, and GameModel. These classes represent the data and business logic of the Book Scrabble game. They encapsulate the game rules, scoring mechanisms, game state, and player information. The Model component is responsible for managing the data and implementing the game-specific functionality. The host holds the information for his game and sends it to all of his guests. The host uses the classes ClientModel, ClientSideHandler, HostSideHandler, and MyHostHandler to handle the communication between his Model and the Models of his guests and between his Model to the BookScrabbleServer. This communication allows the updating of the Game based on user input, and propagate changes to the View. To each guest there's a simple implementation of the Model component that receives its updates from the host.  

2. View: The View component represents the user interface of the Book Scrabble game. It includes graphical elements such as buttons, text fields, and game board representation. The View is responsible for displaying the game state to the user and capturing user input. In this project, the View is not explicitly mentioned, but it will be added in the near future.

3. ViewModel: The ViewModel component acts as an intermediary between the Model and the View. It provides a layer of abstraction that exposes the necessary data    and functionality from the Model to the View. The ViewModel translates the user's interactions with the View into actions that affect the underlying Model. It also notifies the View of any changes in the Model, allowing the View to update its display accordingly. In this project, the ViewModel is not explicitly mentioned, but it will be added in the near future.

By adopting the MVVM architecture, the project achieves separation of concerns and improves the maintainability and testability of the client-side implementation. The Model encapsulates the game logic and data, making it reusable and easily testable. The ViewModel serves as a bridge between the Model and the View, allowing for decoupling and easier management of the user interface. The use of MVVM promotes modular design, code reusability, and flexibility in extending or modifying the application's functionality.


Testing:
The project includes a Test package for testing the different classes of the project in the style of Unit Testing.

Getting Started:
To run the project, follow these steps:

Compile the source code.
Start the server by running the MyServer class with the desired port number.
Run the client application using the TestRunClient class.
Choose the appropriate options (host/guest, server IP, and port) when prompted.
Play the Book Scrabble game by adding words to the board, searching for words in the dictionary and challenging their existence in dictionary's.
Note: The project assumes that the necessary input files, such as the dictionary and book files, are available in the specified locations.

Dependencies:
The project relies on the following dependencies:
1. Java SE Development Kit (JDK)
2. Java Standard Library

Contributors:
Itay Evron
Ofek Shpirer
Maya Geva
Uri Beeri
