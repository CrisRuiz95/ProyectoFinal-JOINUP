const STAR_EMPTY = 'img/star-empty.png';
const STAR_HALF  = 'img/star-half.png';
const STAR_FILL  = 'img/star-fill.png';

function initRating(ratingWrapper) {
    const starsContainer = ratingWrapper.querySelector('.stars');
    const range = ratingWrapper.querySelector('.rating-range');

    function renderStars(value) {
        starsContainer.innerHTML = '';
        for (let i = 1; i <= 5; i++) {
            const star = document.createElement('div');
            star.className = 'star';
            if (value >= i) {
                star.style.backgroundImage = `url(${STAR_FILL})`;
            } else if (value >= i - 0.5) {
                star.style.backgroundImage = `url(${STAR_HALF})`;
            } else {
                star.style.backgroundImage = `url(${STAR_EMPTY})`;
            }
            starsContainer.appendChild(star);
        }
    }

    function posToValue(clientX) {
        const rect = ratingWrapper.getBoundingClientRect();
        let rel = (clientX - rect.left) / rect.width;
        rel = Math.max(0, Math.min(rel, 1));
        return Math.round(rel * 10) / 2; // redondeo a 0.5
    }

    function setValue(v) {
        range.value = v;
        renderStars(v);
    }

    range.addEventListener('input', () => {
        renderStars(parseFloat(range.value));
    });

    let dragging = false;

    ratingWrapper.addEventListener('pointerdown', e => {
        dragging = true;
        ratingWrapper.setPointerCapture(e.pointerId);
        setValue(posToValue(e.clientX));
    });

    ratingWrapper.addEventListener('pointermove', e => {
        if (!dragging) return;
        setValue(posToValue(e.clientX));
    });

    ratingWrapper.addEventListener('pointerup', e => {
        dragging = false;
        ratingWrapper.releasePointerCapture(e.pointerId);
    });

    ratingWrapper.addEventListener('pointercancel', () => dragging = false);

    renderStars(parseFloat(range.value));
}

document.querySelectorAll('.rating-wrapper').forEach(initRating);
