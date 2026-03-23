type ViewTransitionDocument = Document & {
  startViewTransition?: (callback: () => void) => void
}

export function runViewTransition(update: () => void) {
  const transitionDocument = document as ViewTransitionDocument

  if (typeof transitionDocument.startViewTransition === 'function') {
    transitionDocument.startViewTransition(update)
    return
  }

  update()
}
