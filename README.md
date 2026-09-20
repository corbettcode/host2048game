# 2048

A [Kobweb](https://github.com/varabyte/kobweb) implementation of
[2048](https://en.wikipedia.org/wiki/2048_(video_game)): slide the tiles with the arrow keys (or WASD, or a swipe),
merge matching numbers, and try to build a 2048 tile.

The rules follow the ones described on Wikipedia:

* A 4x4 grid that starts with two tiles.
* Every turn spawns one new tile in a random empty cell: a 2 90% of the time, a 4 otherwise.
* Tiles slide as far as they can; equal tiles that collide merge and add their combined value to the score.
* A tile formed by a merge cannot merge again during the same move.
* Reaching 2048 wins, but you can keep playing for a higher score.
* The game ends when there are no empty cells and no equal neighbours left.

## Layout

| Path | What it holds |
| --- | --- |
| `site/src/jsMain/.../game/Game2048.kt` | The rules, as pure functions over an immutable `GameState`. |
| `site/src/jsMain/.../game/GameController.kt` | Live game state plus the best score, persisted in local storage. |
| `site/src/jsMain/.../components/widgets/` | The board, tiles, score boxes and buttons. |
| `site/src/jsMain/.../pages/Index.kt` | Page layout, keyboard and swipe input, responsive board sizing. |
| `site/src/jsTest/.../game/Game2048Test.kt` | Rule tests, run with `./gradlew :site:jsTest`. |

Tiles keep a stable id as they move, so the UI can animate them sliding between cells. When two tiles merge, the
merged tile inherits the id of the tile that slid into place, which is what makes a merge look like a single
uninterrupted slide.

## Getting Started

First, run the development server by typing the following command in a terminal under the `site` folder:

```bash
$ cd site
$ kobweb run
```

Open [http://localhost:8080](http://localhost:8080) with your browser to see the result.

You can use any editor you want for the project, but we recommend using **IntelliJ IDEA Community Edition** downloaded
using the [Toolbox App](https://www.jetbrains.com/toolbox-app/).

Press `Q` in the terminal to gracefully stop the server.

### Live Reload

Feel free to edit / add / delete new components, pages, and API endpoints! When you make any changes, the site will
indicate the status of the build and automatically reload when ready.

## Exporting the Project

When you are ready to ship, you should shutdown the development server and then export the project using:

```bash
kobweb export
```

When finished, you can run a Kobweb server in production mode:

```bash
kobweb run --env prod
```

If you want to run this command in the Cloud provider of your choice, consider disabling interactive mode since nobody
is sitting around watching the console in that case anyway. To do that, use:

```bash
kobweb run --env prod --notty
```

Kobweb also supports exporting to a static layout which is compatible with static hosting providers, such as GitHub
Pages, Netlify, Firebase, any presumably all the others. You can read more about that approach here:
https://bitspittle.dev/blog/2022/staticdeploy