
import React from 'react';
import './Tile.css';

const Tile = ({ tile, onClick, selected }) => {
    if (!tile) return <div className="tile empty"></div>;

    const { suit, value, id } = tile;

    // Basic visual representation
    const getLabel = () => {
        if (suit === 'bamboo') return `Bamboo ${value}`;
        if (suit === 'character') return `Char ${value}`;
        if (suit === 'dot') return `Dot ${value}`;
        if (suit === 'wind') return `${value} Wind`;
        if (suit === 'dragon') return `${value} Dragon`;
        return 'Unknown';
    };

    return (
        <div
            className={`tile ${selected ? 'selected' : ''} ${suit}`}
            onClick={() => onClick(tile)}
        >
            <span className="tile-value">{value}</span>
            <span className="tile-suit">{suit}</span>
        </div>
    );
};

export default Tile;
