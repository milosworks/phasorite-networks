# 0.0.1-beta

## Breaking Changes

* **Only Phasorite Buds drop Phasorite Dust, Phasorite Clusters will not drop Phasorite Dust anymore.** Use
  smelting/blasting recipe to make dust from
  crystals.
* **Phasorite Components in the inventory might lead to a minor bug.** Due to changes in the nbt tag names if you have a
  phasorite importer/exporter in the inventory and place it, their name may change to the name of the network.

## What's Changed

* Phasorite Clusters and Buds are now dropped by silktouch.
* fixed scroll issue on text inputs.
* added AE2 Charger recipe Phasorite Crystal -> Charged Phasorite Crystal.
* added furnace recipe Phasorite Crystal -> Phasorite Dust.
* fixed inventory title showing on screen.
* fixed writing E (or keybind used to open inventory) closes screen while writing.
* fixed blurriness option compatibility, so we don't show blur if player has the no blur option.
* added jade integration.
* fixed a bug where the components eat your blocks.
* added component info on tooltips.
* fixed a bug when a player does not place a component it doesn't let you open it.
* added full translation support