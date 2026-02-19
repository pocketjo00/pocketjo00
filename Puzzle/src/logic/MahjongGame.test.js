
import { describe, it, expect, beforeEach } from 'vitest';
import { MahjongGame, SUITS } from './MahjongGame';

describe('MahjongGame', () => {
    let game;

    beforeEach(() => {
        game = new MahjongGame();
    });

    it('should initialize with 136 tiles', () => {
        // 3 suits * 9 values * 4 copies = 108
        // 4 winds * 4 copies = 16
        // 3 dragons * 4 copies = 12
        // Total = 136
        // However, after deal, tiles are in wall + hands
        // Wall should have 136 - (13 * 4) = 84 tiles
        expect(game.wall.length).toBe(84);
        expect(game.players[0].length).toBe(13);
        expect(game.players[1].length).toBe(13);
        expect(game.players[2].length).toBe(13);
        expect(game.players[3].length).toBe(13);
    });

    it('should draw a tile correctly', () => {
        const initialHandSize = game.players[0].length;
        const initialWallSize = game.wall.length;

        const tile = game.draw(0);

        expect(tile).toBeDefined();
        expect(game.players[0].length).toBe(initialHandSize + 1);
        expect(game.wall.length).toBe(initialWallSize - 1);
    });

    it('should discard a tile correctly', () => {
        game.draw(0); // Ensure 14 tiles
        const initialHandSize = game.players[0].length;

        const discardedTile = game.discard(0, 0); // Discard first tile

        expect(discardedTile).toBeDefined();
        expect(game.players[0].length).toBe(initialHandSize - 1);
        expect(game.discards.length).toBe(1);
        expect(game.discards[0].tile).toEqual(discardedTile);
        expect(game.discards[0].player).toBe(0);
    });

    it('should create valid tiles', () => {
        // Check a few tiles to ensure they have correct properties
        // We can't access original tiles array directly as it's modified during init/shuffle
        // But we can check the wall
        const sampleTile = game.wall[0];
        expect(sampleTile).toHaveProperty('suit');
        expect(sampleTile).toHaveProperty('value');
        expect(sampleTile).toHaveProperty('id');
    });
});
