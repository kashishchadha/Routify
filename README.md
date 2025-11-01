# Interactive Network Routing Simulator

A comprehensive Java-based interactive simulator that allows users to construct network topologies (routers and links), assign link costs, and visualize routing algorithms (Distance Vector and Link State) in real time.

## Features

- **Interactive Network Topology Builder**
  - Add and remove routers (nodes) visually
  - Create links (edges) between routers with custom costs
  - Drag and drop routers to reposition them on the canvas
  - Visual representation of network topology with costs displayed on links

- **Routing Algorithms**
  - **Distance Vector Routing (Bellman-Ford)**: Implements the distance vector routing algorithm where routers exchange routing tables until convergence
  - **Link State Routing (Dijkstra's Algorithm)**: Implements link state routing using Dijkstra's shortest path algorithm

- **Real-time Visualization**
  - Animated routing table updates during algorithm execution
  - Visual highlighting of source router
  - Display of routing tables with Destination, Cost, and Next Hop columns

- **User Interface**
  - Left Panel: Controls for adding routers, links, and running algorithms
  - Center Panel: Interactive canvas for visualizing and manipulating the network
  - Right Panel: Algorithm settings (source selection, link cost assignment, routing table display)

## Project Structure

```
src/
 ├── gui/
 │   ├── MainFrame.java       # Main application window
 │   ├── LeftPanel.java       # Left control panel
 │   ├── CenterPanel.java     # Central canvas for visualization
 │   └── RightPanel.java      # Right settings panel
 ├── models/
 │   ├── Router.java          # Router (node) model
 │   ├── Link.java            # Link (edge) model
 │   └── NetworkGraph.java    # Network topology manager
 ├── algorithms/
 │   ├── DistanceVector.java  # Distance Vector routing implementation
 │   └── LinkState.java       # Link State routing implementation
 ├── simulation/
 │   └── SimulationEngine.java # Simulation coordinator
 └── App.java                 # Application entry point
```

## Requirements

- Java Development Kit (JDK) 8 or higher
- Java Swing (included with JDK)

## Compilation and Execution

### Quick Start (Windows)

1. **Compile**: Double-click `compile.bat` or run from command prompt:
   ```batch
   compile.bat
   ```

2. **Run**: Double-click `run.bat` or run:
   ```batch
   run.bat
   ```

### Quick Start (Linux/Mac)

1. **Make scripts executable**:
   ```bash
   chmod +x compile.sh run.sh
   ```

2. **Compile**:
   ```bash
   ./compile.sh
   ```

3. **Run**:
   ```bash
   ./run.sh
   ```

### Manual Compilation

Navigate to the project root directory and compile all Java files:

```bash
mkdir -p out
javac -d out -encoding UTF-8 src/models/*.java src/algorithms/*.java src/simulation/*.java src/gui/*.java src/App.java
```

### Manual Execution

```bash
java -cp out App
```

## Usage Instructions

### 1. Building the Network Topology

1. **Add Routers**: Click the "Add Router" button in the left panel. A new router will appear in the center of the canvas. Routers are automatically named R1, R2, R3, etc.

2. **Reposition Routers**: Click and drag any router to move it to a different position on the canvas.

3. **Add Links**: Click the "Add Link" button. A dialog will appear where you can:
   - Select source router
   - Select destination router
   - Enter link cost (integer)
   - Click OK to create the link

4. **Assign/Edit Link Costs**: Click "Assign Link Cost" in the right panel. Select the routers and enter the new cost value.

### 2. Running Routing Algorithms

1. **Select Source Router**: Use the "Select Source Node" dropdown in the right panel to choose the starting router.

2. **Choose Algorithm**: Select either "Distance Vector" or "Link State" from the algorithm dropdown in the left panel.

3. **Run Algorithm**: Click "Run Algorithm" button. The simulation will start and:
   - The source router will be highlighted
   - Routing tables will update in real-time
   - Status updates will appear in the status bar

4. **View Results**: After the algorithm completes:
   - Click "Display Routing Tables" in the right panel
   - A dialog will show routing tables for all routers
   - Tables display: Destination, Cost, and Next Hop

### 3. Sample Scenario

To recreate the example from the specification:

1. Add 5 routers (R1, R2, R3, R4, R5) - you can rename them later if needed
2. Create links with the following costs:
   - A-B = 3 (connect R1-R2 with cost 3)
   - A-C = 8 (connect R1-R3 with cost 8)
   - B-C = 2 (connect R2-R3 with cost 2)
   - C-D = 1 (connect R3-R4 with cost 1)
   - D-E = 4 (connect R4-R5 with cost 4)
   - C-E = 6 (connect R3-R5 with cost 6)
   - B-D = 5 (connect R2-R4 with cost 5)
3. Select R1 (or router A) as the source
4. Choose "Distance Vector" algorithm
5. Click "Run Algorithm"
6. View the routing tables after completion

## Algorithm Details

### Distance Vector Routing

- Each router maintains a routing table with destination, cost, and next hop
- Routers exchange routing tables with neighbors iteratively
- Algorithm converges when no more updates occur
- Updates are animated step-by-step for visualization

### Link State Routing

- Each router has full knowledge of network topology
- Uses Dijkstra's algorithm to compute shortest paths
- Computes paths from source router to all destinations
- Results are displayed immediately after computation

## GUI Components

### Left Panel
- **Add Router**: Creates a new router node
- **Add Link**: Creates a link between two routers
- **Algorithm Selection**: Choose Distance Vector or Link State
- **Run Algorithm**: Starts the simulation

### Center Panel
- **Canvas**: Visual representation of the network
- **Routers**: Displayed as circles with names
- **Links**: Displayed as lines with cost labels
- **Drag & Drop**: Click and drag routers to reposition

### Right Panel
- **Assign Link Cost**: Edit link costs between routers
- **Select Source Node**: Choose the starting router for algorithms
- **Display Routing Tables**: View all routing tables in a dialog

## Technical Implementation

- **Architecture**: Object-oriented design with clear separation of concerns
- **GUI Framework**: Java Swing with custom painting
- **Concurrency**: Swing Timer for non-blocking animations
- **Data Structures**: HashMap, ArrayList, PriorityQueue for efficient algorithms
- **Design Patterns**: Observer pattern for GUI updates, Model-View separation

## Future Enhancements (Optional)

- Save/Load network topologies to/from file
- Step-by-step execution mode
- Animation speed control
- Color highlighting of shortest paths
- Remove router/link functionality
- Export routing tables to file

## Troubleshooting

- **Algorithm doesn't run**: Make sure at least one router exists and a source is selected
- **Link not created**: Ensure source and destination are different routers
- **Tables not updating**: Wait for algorithm to complete, then click "Display Routing Tables"
- **GUI not responsive**: Ensure you're using Java 8 or higher

## License

This project is provided as-is for educational purposes.

## Author

Interactive Network Routing Simulator - Java Implementation

---

**Note**: This simulator is designed for educational purposes to demonstrate routing algorithms in computer networks. The visualization and step-by-step updates help understand how routing algorithms work in practice.

