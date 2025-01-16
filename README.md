<div align="center">
    <img src="https://i.imgur.com/mPZwzHI.png" alt="Phasorite Networks">
</div>
<br>
<div align="center">
    <!-- All in one line is actually a bug fix -->
    <img alt="Available For NeoForge" height="56" src="https://raw.githubusercontent.com/intergrav/devins-badges/8494ec1ac495cfb481dc7e458356325510933eb0/assets/cozy/supported/neoforge_vector.svg">
    <a href="https://modrinth.com/mod/phasorite-networks"><img alt="Available On Modrinth" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg"></a>
    <a href="https://www.curseforge.com/minecraft/mc-mods/phasorite-networks"><img alt="Available On CurseForge" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/curseforge_vector.svg"></a>
    <br>    
    <a href="https://discord.gg/dVPqq2U4xy"><img alt="discord-singular" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/social/discord-singular_vector.svg"></a>
    <a href="https://ko-fi.com/xyz.milosworks"><img alt="kofi-singular" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/donate/kofi-singular_vector.svg"></a>
</div>

## Table of Contents

- [About](#about)
- [Networks](#networks)
    - [Importer](#phasorite-importer)
    - [Exporter](#phasorite-exporter)
- [Resources](#resources)
    - [Budding Phasorite](#budding-phasorite)
    - [Phasorite Clusters And Buds](#phasorite-clusters-and-buds)
    - [Others](#other-materials)
- [License](#license)
- [Issues](#issues)
- [Contribution](#contribution)

## About

Phasorite Networks is a modern (WIP, Alpha) mod for transferring energy wirelessly.

### Networks

**The base of Phasorite Networks are, well, networks!**

Networks are fully customizable, with the ability to scale across various dimensions, you can manage and see the
throughput of energy across the network.
Using a network reduces the lag that a huge **cable** network could cause, also reducing the time for building the
infrastructure for the cables.

### Component Screen

- C: Component Tab, shows information of the current component
- N: Networks Tab, shows all accessible networks
- S: Statistics Tab, shows statistics of the network
- C: Component**s** Tab, shows all components in the network
- M: Members Tab, shows all members (players) connected to the network

<img height="256" src="https://i.imgur.com/UPeE3AO.png">

- Set a custom name for your component (Importer/Exporter)
- Set a limit of the energy
- Set a priority
    - Exporter: First energy out
    - Importer: First energy in
- Override mode ignores the others priority and prioritizes this one first
- Limitless mode ignores the set limit of the component

<img height="256" src="https://i.imgur.com/7ZxrHyA.png">

**Create a network**

- Use a color picker to choose the color of your network!
- Set your network to a specific visibility!
    - Private
    - Public
    - Public with Password (Only people with the password can join)

<img height="256" src="https://i.imgur.com/DeETLrp.png">

- DE: Delete current network
- E: Edit current network
- D: Disconnect from current network
- C: Create a new network

<img height="256" src="https://i.imgur.com/viBeKO9.png">

#### Phasorite Importer

The phasorite importer is probably the first component you'll need to craft, this component receives energy to transfer
it to all the exporters in the network.

<div align="center">
    <img height="256" src="https://i.imgur.com/lSL0ff8.png">
    <img height="128" src="https://i.imgur.com/df8TAbU.png">
</div>

#### Phasorite Exporter

The phasorite exporter is the second component you'll craft, this component exports the energy that the importers
received.

<div align="center">
    <img height="256" src="https://i.imgur.com/T37rarI.png">
    <img height="128" src="https://i.imgur.com/wx7ySAP.png">
</div>

### Resources

You need resources to make the components, these resources are craftable or obtainable in the world.

#### Budding Phasorite

The Budding Phasorite will be your best friend, its made by using a [Phasorite Seed](#phasorite-seed) on a Budding
Amethyst.
It produces [Phasorite Clusters](#phasorite-clusters-and-buds), these clusters when fully grown will give you Phasorite
Crystals or Charged Phasorite Crystals

<div align="center">
  <img height="256" src="https://i.imgur.com/4rAG8M5.png">
</div>

#### Phasorite Clusters And Buds

These are the phasorite clusters and buds.
Small Bud, Medium Bud, Large Bud, Phasorite Cluster and Charged Phasorite Cluster (From left to right, charged clusters
have an animation).

Each bud will give you Phasorite Dust, the Phasorite Cluster gives you Phasorite Crystals, the Charged Phasorite Cluster
gives Charged Phasorite Crystals.

To grow the Charged Phasorite Clusters, these will need to be on direct sunlight.

<div align="center">
  <img height="128" src="https://i.imgur.com/2jRlzvs.png">
</div>

#### Other Materials

These materials are just craftable and needed to make components, etc.

#### Phasorite Dust

Phasorite dust can be obtained by smelting/blasting phasorite crystals or by breaking buds.

#### Phasorite Core

Needed in every component.

<div align="center">
  <img height="128" src="https://i.imgur.com/BWqW1Lj.png">
  <img height="128" src="https://i.imgur.com/N52GcJZ.png">
</div>

#### Phasorite Lens

Needed in every component.

<div align="center">
  <img height="128" src="https://i.imgur.com/3xDPYsR.png">
  <img height="128" src="https://i.imgur.com/Zp6OU2F.png">
</div>

#### Phasorite Seed

Converts Budding Amethyst into Budding Phasorite

<div align="center">
  <img height="128" src="https://i.imgur.com/apafoOA.png">
  <img height="128" src="https://i.imgur.com/3VhnoyF.png">
</div>

## License

- Phasorite Networks
    - (c) 2024 Milosworks
    - [![License: LGPL v3](https://img.shields.io/badge/License-AGPL_v3-blue.svg)](https://www.gnu.org/licenses/agpl-3.0)

- [Textures](src/main/resources/assets/phasoritenetworks/textures)
  and [Models](src/main/resources/assets/phasoritenetworks/models)

    - (c) 2024 Milosworks
    - [![License](https://img.shields.io/badge/License-CC%20BY--NC--SA%204.0-yellow.svg?style=flat-square)](https://creativecommons.org/licenses/by-nc-sa/4.0)

<sub>thanks Jm3 for the help in the textures</sub>

## Issues

- Found an bug or have a suggestion?
  Go to [the issues page](https://github.com/xyz.milosworks/phasorite-networks/issues) and
  click [new issue](https://github.com/xyz.milosworks/phasorite-networks/issues/new)

## Contribution

- To contribute to Phasorite Networks, fork and create
  a [Pull-Request](https://help.github.com/articles/creating-a-pull-request)
- You might want to discuss with us in the [Phasorite Networks Discord](https://discord.gg/dVPqq2U4xy) before making a
  PR

