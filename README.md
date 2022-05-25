# Code Wars (backend)

_Under development._

See the live demo at [https://codewars.sajansen.nl/](https://codewars.sajansen.nl/).

## Goal

The goal of this project is to create a fun challenge while coding.

You have to code the controls your own character in this multiplayer game. The only important thing this game provides is the logic to keep all players in sync. From there, it's up to you to create the best computerized battle tank in history.

## Features

The game is actually just a proof of concept. The proof has been delivered, so now it's just under slow development. Players can code their tank to drive around and that's basically it. The idea is to implement fire mechanisms, obstacles, objectives, and maybe even multiple types of vehicles, like drones.

### Sync / anti-cheating

The game is played on the frontend (see my repo [https://github.com/sampie777/codewars](codewars)). This backend calculates all the physics and player positions. And communicates this with the connected players. In this way, the player may take control of the whole frontend, without influencing the game physics/mechanics. The player is actually free (and a little bit encouraged) to do so: customizing the whole frontend. 
