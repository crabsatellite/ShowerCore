from PIL import Image, ImageDraw
import random

def generate_shower_texture(output_path):
    width, height = 16, 16
    img = Image.new('RGBA', (width, height), (0, 0, 0, 0))
    pixels = img.load()

    # Colors
    bg_color = (200, 200, 200, 255) # Lighter background
    border_color = (100, 100, 100, 255) # Darker border
    hole_color = (20, 20, 20, 255) # Almost black holes
    
    # 1. Fill Background with Noise
    for y in range(height):
        for x in range(width):
            noise = random.randint(-10, 10)
            r = max(0, min(255, bg_color[0] + noise))
            g = max(0, min(255, bg_color[1] + noise))
            b = max(0, min(255, bg_color[2] + noise))
            pixels[x, y] = (r, g, b, 255)

    # 2. Draw Border
    # Simple 1px border
    for x in range(width):
        pixels[x, 0] = border_color
        pixels[x, height-1] = border_color
    for y in range(height):
        pixels[0, y] = border_color
        pixels[width-1, y] = border_color
        
    # Add some noise to border too
    for y in range(height):
        for x in range(width):
            if x == 0 or x == width-1 or y == 0 or y == height-1:
                noise = random.randint(-10, 10)
                r = max(0, min(255, border_color[0] + noise))
                g = max(0, min(255, border_color[1] + noise))
                b = max(0, min(255, border_color[2] + noise))
                pixels[x, y] = (r, g, b, 255)

    # 3. Draw Holes
    # Create a regular grid of holes in the center
    # Center area: x from 3 to 12, y from 3 to 12
    # Let's do a 3x3 grid of 2x2 holes? Or just single pixel holes?
    # Single pixel holes look better for 16x16
    
    # Pattern: Symmetric Grid
    # To maintain symmetry on 16x16 with non-adjacent pixels, we need a wider gap in the center
    # or specific symmetric coordinates.
    # Center is 7.5.
    # Pairs: (2,13), (4,11), (6,9)
    # This gives gaps of 1px between 2-4, 4-6, 9-11, 11-13
    # And a gap of 2px between 6-9 (center)
    
    coords = [2, 4, 6, 9, 11, 13]
    
    holes = []
    
    for y in coords:
        for x in coords:
            holes.append((x, y))
            
            # Make hole slightly larger? No, 1px is fine for shower head holes
            pixels[x, y] = hole_color
            
            # Add a slight highlight below/right to give depth?
            if x+1 < width and y+1 < height:
                 pixels[x+1, y+1] = (210, 210, 210, 255) # Highlight

    img.save(output_path)
    print(f"Generated {output_path}")

if __name__ == "__main__":
    generate_shower_texture("src/references/showerpom_generated.png")
