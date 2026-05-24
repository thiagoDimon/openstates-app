export const USParty = {
  DEMOCRATIC: 'Democratic',
  REPUBLICAN: 'Republican',
  INDEPENDENT: 'Independent',
} as const

export type USParty = (typeof USParty)[keyof typeof USParty]

export const US_PARTY_OPTIONS = Object.values(USParty)
