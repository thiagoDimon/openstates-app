export const USParty = {
  DEMOCRATIC: 'Democratic',
  REPUBLICAN: 'Republican',
} as const

export type USParty = (typeof USParty)[keyof typeof USParty]

export const US_PARTY_OPTIONS = Object.values(USParty)
