# Project 3 Prep

**For tessellating hexagons, one of the hardest parts is figuring out where to place each hexagon/how to easily place hexagons on screen in an algorithmic way.
After looking at your own implementation, consider the implementation provided near the end of the lab.
How did your implementation differ from the given one? What lessons can be learned from it?**

Answer: The helper methods that I created did not efficiently calculate how to place the hexagons for each section within the larger tesselation. 
I would think about writing additional helper methods and another class within the package that would allow for better abstraction and easier debugging.

-----

**Can you think of an analogy between the process of tessellating hexagons and randomly generating a world using rooms and hallways?
What is the hexagon and what is the tesselation on the Project 3 side?**

Answer: In relation to project 3, the tesselation would be the world that consists of hexagons, representative of rooms and hallways.

-----
**If you were to start working on world generation, what kind of method would you think of writing first? 
Think back to the lab and the process used to eventually get to tessellating hexagons.**

Answer: I would start with creating a method that allows me to compute the position of the hex within the world.

-----
**What distinguishes a hallway from a room? How are they similar?**

Answer: The difference that distinguishes a hallway from a room is in the fact that rooms have distinct square/rectangle like uniformity whereas
 a hallway will have long, 1-tile width structures.
