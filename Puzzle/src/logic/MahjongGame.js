
export const SUITS = {
  BAMBOO: 'bamboo',
  CHARACTER: 'character',
  DOT: 'dot',
  WIND: 'wind',
  DRAGON: 'dragon'
};

export const WINDS = ['east', 'south', 'west', 'north'];
export const DRAGONS = ['red', 'green', 'white'];

export class MahjongGame {
  constructor() {
    this.tiles = [];
    this.wall = [];
    this.players = [[], [], [], []]; // 0 is human, 1-3 are AI (or future multiplayer)
    this.currentPlayer = 0;
    this.discards = [];
    this.init();
  }

  init() {
    this.tiles = this.generateTiles();
    this.shuffle();
    this.deal();
  }

  generateTiles() {
    const tiles = [];
    // Numbered suits (1-9)
    [SUITS.BAMBOO, SUITS.CHARACTER, SUITS.DOT].forEach(suit => {
      for (let i = 1; i <= 9; i++) {
        for (let j = 0; j < 4; j++) {
          tiles.push({ suit, value: i, id: `${suit}-${i}-${j}` });
        }
      }
    });

    // Winds
    WINDS.forEach(wind => {
        for (let j = 0; j < 4; j++) {
            tiles.push({ suit: SUITS.WIND, value: wind, id: `wind-${wind}-${j}` });
        }
    });

    // Dragons
    DRAGONS.forEach(dragon => {
        for (let j = 0; j < 4; j++) {
            tiles.push({ suit: SUITS.DRAGON, value: dragon, id: `dragon-${dragon}-${j}` });
        }
    });
    
    return tiles;
  }

  shuffle() {
    for (let i = this.tiles.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [this.tiles[i], this.tiles[j]] = [this.tiles[j], this.tiles[i]];
    }
    this.wall = [...this.tiles];
  }

  deal() {
    // Standard 13 tiles per player
    for (let i = 0; i < 13; i++) {
      for (let p = 0; p < 4; p++) {
        if (this.wall.length > 0) {
          this.players[p].push(this.wall.pop());
        }
      }
    }
    this.sortHand(0); // Sort human player's hand
  }

  draw(playerIndex) {
    if (this.wall.length === 0) return null;
    const tile = this.wall.pop();
    this.players[playerIndex].push(tile);
    if (playerIndex === 0) this.sortHand(0);
    return tile;
  }

  discard(playerIndex, tileIndex) {
    const hand = this.players[playerIndex];
    if (tileIndex < 0 || tileIndex >= hand.length) return null;
    
    const tile = hand.splice(tileIndex, 1)[0];
    this.discards.push({ player: playerIndex, tile });
    return tile;
  }

  sortHand(playerIndex) {
    this.players[playerIndex].sort((a, b) => {
      if (a.suit !== b.suit) return a.suit.localeCompare(b.suit);
      if (typeof a.value === 'number' && typeof b.value === 'number') return a.value - b.value;
      return String(a.value).localeCompare(String(b.value));
    });
  }

  getGameState() {
    return {
      hand: this.players[0],
      discards: this.discards,
      wallCount: this.wall.length,
      currentPlayer: this.currentPlayer
    };
  }
}
