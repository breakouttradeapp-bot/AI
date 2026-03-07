"""
AI Kundli Generator – FastAPI Backend
Install: pip install fastapi uvicorn pyephem geopy reportlab pillow
Run:     uvicorn main:app --host 0.0.0.0 --port 8000
"""

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import Optional, List
import ephem
import math
import base64
import io
from datetime import datetime, date
from PIL import Image, ImageDraw, ImageFont
import os

app = FastAPI(title="AI Kundli Generator API", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

# ── Schemas ────────────────────────────────────────────────────────────────

class KundliRequest(BaseModel):
    name: str
    gender: str
    date_of_birth: str          # "YYYY-MM-DD"
    time_of_birth: str          # "HH:MM"
    latitude: float
    longitude: float
    timezone: str = "Asia/Kolkata"

class PlanetPosition(BaseModel):
    planet: str
    sign: str
    degree: float
    house: int
    is_retro: bool = False

class HouseInfo(BaseModel):
    house: int
    sign: str
    degree: float

class KundliResponse(BaseModel):
    success: bool
    chart_image_base64: Optional[str]
    planets: List[PlanetPosition]
    houses: List[HouseInfo]
    ascendant: str
    moon_sign: str
    sun_sign: str

class CompatibilityRequest(BaseModel):
    person1: KundliRequest
    person2: KundliRequest

class GunaMilanDetail(BaseModel):
    name: str
    max_score: int
    score: int

class CompatibilityResponse(BaseModel):
    guna_milan_score: int
    compatibility_percent: int
    is_manglik1: bool
    is_manglik2: bool
    advice: str
    details: List[GunaMilanDetail]

class ZodiacHoroscope(BaseModel):
    sign: str
    symbol: str
    text: str
    lucky_number: int
    lucky_color: str

# ── Helpers ────────────────────────────────────────────────────────────────

ZODIAC_SIGNS = [
    "Aries","Taurus","Gemini","Cancer","Leo","Virgo",
    "Libra","Scorpio","Sagittarius","Capricorn","Aquarius","Pisces"
]
ZODIAC_SYMBOLS = ["♈","♉","♊","♋","♌","♍","♎","♏","♐","♑","♒","♓"]

PLANETS = {
    "Sun":     ephem.Sun,
    "Moon":    ephem.Moon,
    "Mars":    ephem.Mars,
    "Mercury": ephem.Mercury,
    "Jupiter": ephem.Jupiter,
    "Venus":   ephem.Venus,
    "Saturn":  ephem.Saturn,
}

def get_sign(longitude_deg: float) -> tuple[str, float]:
    idx     = int(longitude_deg / 30) % 12
    degree  = longitude_deg % 30
    return ZODIAC_SIGNS[idx], round(degree, 2)

def compute_planets(req: KundliRequest) -> tuple[list, str, str, str]:
    dt_str  = f"{req.date_of_birth} {req.time_of_birth}"
    dt      = datetime.strptime(dt_str, "%Y-%m-%d %H:%M")

    observer = ephem.Observer()
    observer.lat  = str(req.latitude)
    observer.lon  = str(req.longitude)
    observer.date = dt.strftime("%Y/%m/%d %H:%M:%S")

    positions = []
    sun_sign  = "Aries"
    moon_sign = "Aries"

    for name, PlanetClass in PLANETS.items():
        p = PlanetClass(observer)
        p.compute(observer)
        lon_deg = math.degrees(float(repr(p.hlong).replace(":", " ").replace(" ", ".")) if hasattr(p, 'hlong') else float(p.ra))
        # Use ecliptic longitude
        ecl = ephem.Ecliptic(p, epoch=observer.date)
        lon_deg = math.degrees(float(ecl.lon)) % 360
        sign, deg = get_sign(lon_deg)
        house = (int(lon_deg / 30)) % 12 + 1
        is_retro = False
        if hasattr(p, '_ra'):
            is_retro = False  # simplified; real retro calc needs ephemeris

        positions.append(PlanetPosition(
            planet   = name,
            sign     = sign,
            degree   = deg,
            house    = house,
            is_retro = is_retro
        ))
        if name == "Sun":  sun_sign  = sign
        if name == "Moon": moon_sign = sign

    # Ascendant (simplified)
    asc_deg = (float(observer.sidereal_time()) * 15 + req.longitude) % 360
    ascendant, _ = get_sign(asc_deg)
    return positions, ascendant, moon_sign, sun_sign

def generate_kundli_chart_image(planets: list, ascendant: str) -> str:
    """Generate North Indian style Kundli chart as base64 PNG"""
    size   = 600
    img    = Image.new("RGB", (size, size), color=(10, 0, 30))
    draw   = ImageDraw.Draw(img)

    # Outer border
    draw.rectangle([10, 10, size-10, size-10], outline=(106, 5, 114), width=3)

    # North Indian house layout (diamond grid)
    mid = size // 2
    q   = size // 4

    # Grid lines
    for pts in [
        [(q, 10), (mid, q), (3*q, 10)],
        [(3*q, 10), (size-10, q), (size-10, 3*q), (3*q, size-10)],
        [(q, size-10), (10, 3*q), (10, q), (q, 10)],
        [(q, size-10), (mid, 3*q), (3*q, size-10)],
        [(q, 10), (mid, q), (mid, 3*q), (q, size-10)],
        [(3*q, 10), (mid, q), (mid, 3*q), (3*q, size-10)],
        [(10, q), (q, mid), (10, 3*q)],
        [(size-10, q), (3*q, mid), (size-10, 3*q)],
    ]:
        draw.line(pts, fill=(106, 5, 114), width=2)

    # House numbers
    house_positions = [
        (mid, q//2), (3*q + q//4, q//2), (size - q//2, mid - q//4),
        (3*q + q//4, 3*q + q//4), (mid, size - q//2), (q//4, 3*q + q//4),
        (q//4, mid - q//4), (q//4, q//2), (mid - q//4, mid - q//4),
        (mid + q//4, mid - q//4), (mid + q//4, mid + q//4), (mid - q//4, mid + q//4),
    ]
    for i, (x, y) in enumerate(house_positions):
        draw.text((x, y), str(i+1), fill=(255, 215, 0))

    # Planet labels in houses
    house_planet_map: dict[int, list] = {}
    for planet in planets:
        h = planet.house
        house_planet_map.setdefault(h, []).append(planet.planet[:3])

    for h, planet_list in house_planet_map.items():
        if h <= len(house_positions):
            x, y = house_positions[h-1]
            draw.text((x - 20, y + 12), " ".join(planet_list), fill=(200, 180, 255))

    # Ascendant label
    draw.text((mid - 20, q + 5), f"Asc: {ascendant[:3]}", fill=(255, 215, 0))

    # Title
    draw.text((10, size - 25), "AI Kundli Generator", fill=(106, 5, 114))

    buffer = io.BytesIO()
    img.save(buffer, format="PNG")
    return base64.b64encode(buffer.getvalue()).decode()

def compute_guna_milan(p1_moon: str, p2_moon: str) -> CompatibilityResponse:
    """Simplified Guna Milan — production should use full Ashtakuta matching"""
    sign_to_num = {s: i for i, s in enumerate(ZODIAC_SIGNS)}
    n1 = sign_to_num.get(p1_moon, 0)
    n2 = sign_to_num.get(p2_moon, 0)

    varna   = 1 if abs(n1 - n2) < 3 else 0
    vasya   = 2 if abs(n1 - n2) in [0, 1, 7, 11] else 1
    tara    = 3 if (n2 - n1) % 9 not in [3, 5, 7] else 1
    yoni    = 4 if abs(n1 - n2) % 2 == 0 else 2
    maitri  = 5 if abs(n1 - n2) < 4 else 2
    gana    = 6 if n1 % 3 == n2 % 3 else 3
    bhakoot = 7 if abs(n1 - n2) not in [6, 8, 9] else 0
    nadi    = 8 if n1 % 3 != n2 % 3 else 0

    total = varna + vasya + tara + yoni + maitri + gana + bhakoot + nadi
    pct   = int(total / 36 * 100)

    manglik1 = n1 in [0, 3, 6, 7, 10]
    manglik2 = n2 in [0, 3, 6, 7, 10]

    advice_map = {
        pct >= 75: "Excellent compatibility! This is a highly auspicious match.",
        60 <= pct < 75: "Good compatibility. A happy and balanced marriage is foreseen.",
        40 <= pct < 60: "Average compatibility. With mutual understanding, this can work well.",
        pct < 40: "Below average compatibility. Seek astrological guidance before proceeding."
    }
    advice = next(v for k, v in advice_map.items() if k)

    return CompatibilityResponse(
        guna_milan_score       = total,
        compatibility_percent  = pct,
        is_manglik1            = manglik1,
        is_manglik2            = manglik2,
        advice                 = advice,
        details = [
            GunaMilanDetail("Varna",   1,  varna),
            GunaMilanDetail("Vasya",   2,  vasya),
            GunaMilanDetail("Tara",    3,  tara),
            GunaMilanDetail("Yoni",    4,  yoni),
            GunaMilanDetail("Maitri",  5,  maitri),
            GunaMilanDetail("Gana",    6,  gana),
            GunaMilanDetail("Bhakoot", 7,  bhakoot),
            GunaMilanDetail("Nadi",    8,  nadi),
        ]
    )

# ── Endpoints ──────────────────────────────────────────────────────────────

@app.get("/")
def root():
    return {"message": "AI Kundli Generator API is running 🔮"}

@app.post("/kundli/generate", response_model=KundliResponse)
def generate_kundli(req: KundliRequest):
    try:
        planets, ascendant, moon_sign, sun_sign = compute_planets(req)
        chart_b64 = generate_kundli_chart_image(planets, ascendant)
        houses = [HouseInfo(house=i+1, sign=ZODIAC_SIGNS[i], degree=i*30.0) for i in range(12)]
        return KundliResponse(
            success             = True,
            chart_image_base64  = chart_b64,
            planets             = planets,
            houses              = houses,
            ascendant           = ascendant,
            moon_sign           = moon_sign,
            sun_sign            = sun_sign,
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/kundli/compatibility", response_model=CompatibilityResponse)
def check_compatibility(req: CompatibilityRequest):
    try:
        _, _, moon1, _ = compute_planets(req.person1)
        _, _, moon2, _ = compute_planets(req.person2)
        return compute_guna_milan(moon1, moon2)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/horoscope/all", response_model=List[ZodiacHoroscope])
def get_all_horoscopes():
    texts = [
        "Today brings fiery energy. Be bold in your decisions and take initiative.",
        "Stability and material comfort align with your goals. Trust your instincts.",
        "Communication is your superpower today. Express your ideas freely.",
        "Emotional intuition guides you. Spend time with loved ones.",
        "Your natural charisma attracts positive attention. Shine bright today.",
        "Attention to detail rewards you handsomely. Stay organized.",
        "Balance in relationships brings harmony. Seek fair solutions.",
        "Deep transformation is underway. Embrace change with courage.",
        "Adventure and learning open new doors. Explore new horizons.",
        "Hard work and discipline align with long-term success today.",
        "Innovation and original thinking set you apart from the crowd.",
        "Your intuition and creativity flow like a cosmic river today.",
    ]
    colors = ["Red","Green","Yellow","Silver","Gold","Navy","Pink","Crimson","Purple","Brown","Blue","Sea Green"]
    numbers = [9, 6, 5, 2, 1, 4, 7, 8, 3, 10, 11, 12]
    return [
        ZodiacHoroscope(
            sign         = ZODIAC_SIGNS[i],
            symbol       = ZODIAC_SYMBOLS[i],
            text         = texts[i],
            lucky_number = numbers[i],
            lucky_color  = colors[i],
        )
        for i in range(12)
    ]

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
