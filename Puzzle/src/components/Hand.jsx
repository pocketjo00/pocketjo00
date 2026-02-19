
import React from 'react';
import Tile from './Tile';
import './Hand.css';

const Hand = ({ tiles, onTileClick, selectedTileIndex }) => {
    return (
        <div className="hand">
            {tiles.map((tile, index) => (
                <Tile
                    key={tile.id || index}
                    tile={tile}
                    selected={selectedTileIndex === index}
                    onClick={() => onTileClick(index)}
                />
            ))}
        </div>
    );
};

export default Hand;
