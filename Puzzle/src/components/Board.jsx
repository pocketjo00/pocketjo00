
import React, { useState, useEffect } from 'react';
import { MahjongGame } from '../logic/MahjongGame';
import Hand from './Hand';
import Tile from './Tile';
import './Board.css';

const Board = () => {
    const [game, setGame] = useState(null);
    const [gameState, setGameState] = useState(null);
    const [selectedTileIndex, setSelectedTileIndex] = useState(null);
    const [message, setMessage] = useState('Welcome to Mahjong!');

    useEffect(() => {
        const newGame = new MahjongGame();
        setGame(newGame);
        setGameState(newGame.getGameState());
    }, []);

    const handleDraw = () => {
        if (!game) return;
        const tile = game.draw(0);
        if (tile) {
            setGameState(game.getGameState());
            setMessage(`You drew ${tile.suit} ${tile.value}`);
        } else {
            setMessage('Wall is empty!');
        }
    };

    const handleDiscard = () => {
        if (!game || selectedTileIndex === null) return;
        const tile = game.discard(0, selectedTileIndex);
        if (tile) {
            setGameState(game.getGameState());
            setMessage(`You discarded ${tile.suit} ${tile.value}`);
            setSelectedTileIndex(null);

            // Simulate opponent turns (simplified)
            setTimeout(() => {
                // In a real game, AI would play here.
                // For now, let's just say it's your turn again to draw.
                setMessage('Your turn to draw.');
            }, 1000);
        }
    };

    const handleTileClick = (index) => {
        if (gameState.hand.length % 3 === 2) {
            // Active turn (14 tiles), can select to discard
            setSelectedTileIndex(index);
        } else {
            setMessage("You need to draw a tile first!");
        }
    };

    if (!gameState) return <div>Loading...</div>;

    return (
        <div className="board">
            <div className="info-panel">
                <h1>Mahjong Puzzle</h1>
                <p>Wall tiles remaining: {gameState.wallCount}</p>
                <p className="message">{message}</p>
            </div>

            <div className="game-area">
                <div className="discards-area">
                    <h3>Discards</h3>
                    <div className="discards">
                        {gameState.discards.map((d, i) => (
                            <Tile key={i} tile={d.tile} />
                        ))}
                    </div>
                </div>

                <div className="controls">
                    <button
                        onClick={handleDraw}
                        disabled={gameState.hand.length % 3 === 2}
                    >
                        Draw Tile
                    </button>
                    <button
                        onClick={handleDiscard}
                        disabled={selectedTileIndex === null || gameState.hand.length % 3 !== 2}
                    >
                        Discard Selected
                    </button>
                    <button onClick={() => {
                        const newGame = new MahjongGame();
                        setGame(newGame);
                        setGameState(newGame.getGameState());
                        setMessage("New Game Started");
                        setSelectedTileIndex(null);
                    }}>New Game</button>
                </div>

                <div className="player-area">
                    <h3>Your Hand</h3>
                    <Hand
                        tiles={gameState.hand}
                        selectedTileIndex={selectedTileIndex}
                        onTileClick={handleTileClick}
                    />
                </div>
            </div>
        </div>
    );
};

export default Board;
