# Patam-BookScrabble

Please do not touch the master branch!

![image](https://github.com/PatamProject/Patam-BookScrabble/assets/106737885/b0bd65fe-8ea8-45a3-bcee-91ebd95b6a2c)

GANTT: 

![image](https://github.com/PatamProject/Patam-BookScrabble/assets/130273989/c75968b8-209e-47a8-b314-e1bfdac6ff32)

![image](https://github.com/PatamProject/Patam-BookScrabble/assets/130273989/24033bd9-9cd1-4558-87ed-da5c1fdec6a2)

![image](https://github.com/PatamProject/Patam-BookScrabble/assets/130273989/6094ef2c-ccad-466f-8397-873279b5ccdd)


Video URL : https://youtu.be/pEO7jt0Quzs

Project Overview: The goal of this project is to develop a Book Scrabble game with a MVVM architecture and Socket communications. The game allows players to search for words in a dictionary and challenge the existence of words in books. It incorporates caching mechanisms to optimize word queries and utilizes a Bloom filter for efficient word existence checks. The project includes both server-side and client-side implementations.

Server-side Implementation: The server-side implementation consists of several components:

Cache Handler: This package contains classes related to caching mechanisms. It includes the CacheManager class, which manages a cache with a specified size and replacement policy. The CacheReplacementPolicy interface defines the behavior of cache replacement policies such as Least Recently Used (LRU) and Least Frequently Used (LFU). The LRU and LFU classes provide implementations of these policies.

Dictionary: This package handles dictionary operations. The Dictionary class initializes the dictionary using input files and provides methods for word queries and challenges. It also utilizes caching mechanisms to optimize word lookups.

Dictionary Manager: The DictionaryManager class acts as a central manager for multiple dictionaries. It maintains a map of dictionaries and provides methods for querying and challenging words across all dictionaries.

IO Searcher: This package handles file I/O operations. The IOSearcher class includes methods for searching words in files and extracting words from files.

Server Handler: This package contains classes for handling client connections and requests. The BookScrabbleHandler class implements the ClientHandler interface and defines the logic for handling client requests. The MyServer class is responsible for starting and managing the server socket, accepting client connections, and delegating client requests to the appropriate handler.

Client-side Implementation: The client-side implementation includes a RunClient class that represents the client application. It connects to the host server and interacts with it by sending queries and challenges. The client application can be run as a host or a guest, allowing players to join games hosted by others or host their own games. Each host functions as a server for their guests and as a client to the BookScrabbleServer. The connection to each guest remains persistent during the game.

In this project, the client-side implementation can be divided into three main components:

Model: The Model component of the host consists of classes such as Word, Tile, Board, Rack, PlayerModel, GameManager, and GameModel. These classes represent the data and business logic of the Book Scrabble game. They encapsulate the game rules, scoring mechanisms, game state, and player information. The Model component is responsible for managing the data and implementing the game-specific functionality. The host holds the information for their game and sends it to all of their guests. The host uses the classes ClientModel, ClientSideHandler, HostSideHandler, and MyHostHandler to handle the communication between their Model and the Models of their guests and between their Model and the BookScrabbleServer. This communication allows for updating the game based on user input and propagating changes to the View. Each guest has a simple implementation of the Model component that receives updates from the host.

ViewModel: The ViewModel component acts as an intermediary between the Model and the View. It provides a layer of abstraction that exposes the necessary data and functionality from the Model to the View. The ViewModel translates the user's interactions with the View into actions that affect the underlying Model. It also notifies the View of any changes in the Model, allowing the View to update its display accordingly.

View: The View component represents the user interface of the Book Scrabble game. It consists of several FXML pages and corresponding Java classes that handle the functionality of each page. The View is designed to be responsive, providing an optimal user experience across different devices and screen sizes. Additionally, each page and button is connected to the Model through the ViewModel, promoting a separation of concerns and ensuring efficient data flow. Here are the details of the FXML pages, their associated Java classes, and their responsiveness:

Testing: The project includes a Test package for testing the different classes of the project in the style of Unit Testing.

Getting Started: To run the project, follow these steps:

Compile the source code. Start the server by running the MyServer class with the desired port number. Run the client application using the TestRunClient class. Choose the appropriate options (host/guest, server IP, and port) when prompted. Play the Book Scrabble game by adding words to the board, searching for words in the dictionary, and challenging their existence in the dictionary. Note: The project assumes that the necessary input files, such as the dictionary and book files, are available in the specified locations.

Dependencies: The project relies on the following dependencies: Java SE Development Kit (JDK) Java Standard Library

Contributors: Itay Evron Ofek Shpirer Maya Geva Uri Beeri
